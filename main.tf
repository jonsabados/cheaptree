variable "region" {
  default = "us-east-1"
}

variable "accountId" {}
variable "domain" {}

provider "aws" {
  region = "${var.region}"
}

terraform {
  backend "s3" {
    bucket = "cheaptree-state"
    key    = "network/terraform.tfstate"
    region = "us-east-1"
  }
}

data "terraform_remote_state" "network" {
  backend = "s3"
  config {
    bucket = "cheaptree-state"
    key    = "network/terraform.tfstate"
    region = "us-east-1"
  }
}

data "aws_acm_certificate" "api_cert" {
  domain   = "treeapi.${var.domain}"
  statuses = ["ISSUED"]
}

data "aws_route53_zone" "app_domain" {
  name         = "${var.domain}."
  private_zone = false
}

# base role that lambdas will execute as
resource "aws_iam_role" "lambda_base_execution_role" {
  name = "lambda-base-execution-role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

# give base lambda role permissions to actually create logs
resource "aws_iam_policy_attachment" "lambda_base_execution_role_cloudwatch_policy" {
  name = "default"
  roles = ["${aws_iam_role.lambda_base_execution_role.name}"]
  policy_arn = "arn:aws:iam::aws:policy/AWSLambdaExecute"
}

resource "aws_lambda_function" "hello_lambda" {
  filename = "hello/target/hello-1.0-SNAPSHOT.jar"
  function_name = "hello_world"
  role = "${aws_iam_role.lambda_base_execution_role.arn}"
  handler = "com.jshnd.cheaptree.hello.HelloLambda"
  source_code_hash = "${base64sha256(file("hello/target/hello-1.0-SNAPSHOT.jar"))}"
  runtime = "java8"
}

resource "aws_api_gateway_rest_api" "main_api" {
  name        = "CheapTreeAPI"
  description = "REST API for cheap tree"
}

# /hello resource
resource "aws_api_gateway_resource" "hello_world_resource" {
  rest_api_id = "${aws_api_gateway_rest_api.main_api.id}"
  parent_id   = "${aws_api_gateway_rest_api.main_api.root_resource_id}"
  path_part   = "hello"
}

# POST method for hello resource
resource "aws_api_gateway_method" "hello_method" {
  rest_api_id   = "${aws_api_gateway_rest_api.main_api.id}"
  resource_id   = "${aws_api_gateway_resource.hello_world_resource.id}"
  http_method   = "POST"
  authorization = "NONE"
}

# grant api gateway permission to execute lambda
resource "aws_lambda_permission" "apigw_lambda_hello" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.hello_lambda.arn}"
  principal     = "apigateway.amazonaws.com"

  source_arn = "arn:aws:execute-api:${var.region}:${var.accountId}:${aws_api_gateway_rest_api.main_api.id}/*/${aws_api_gateway_method.hello_method.http_method}${aws_api_gateway_resource.hello_world_resource.path}"
}

# map API resource to lambda
resource "aws_api_gateway_integration" "hello_world_resource_integration" {
  rest_api_id             = "${aws_api_gateway_rest_api.main_api.id}"
  resource_id             = "${aws_api_gateway_resource.hello_world_resource.id}"
  http_method             = "${aws_api_gateway_method.hello_method.http_method}"
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${aws_lambda_function.hello_lambda.arn}/invocations"
}

resource "aws_api_gateway_deployment" "main_api_depoyment" {
  depends_on = ["aws_api_gateway_integration.hello_world_resource_integration"]

  rest_api_id = "${aws_api_gateway_rest_api.main_api.id}"
  stage_name  = "prod"
}

resource "aws_api_gateway_domain_name" "main_api_domain_name" {
  domain_name = "treeapi.${var.domain}"

  certificate_arn  = "${data.aws_acm_certificate.api_cert.arn}"
}

resource "aws_route53_record" "treeapi_dns_entry" {
  zone_id = "${data.aws_route53_zone.app_domain.zone_id}"

  name = "${aws_api_gateway_domain_name.main_api_domain_name.domain_name}"
  type = "A"

  alias {
    name                   = "${aws_api_gateway_domain_name.main_api_domain_name.cloudfront_domain_name}"
    zone_id                = "${aws_api_gateway_domain_name.main_api_domain_name.cloudfront_zone_id}"
    evaluate_target_health = true
  }
}

resource "aws_api_gateway_base_path_mapping" "treeapi_domain_base_path_to_prod" {
  api_id      = "${aws_api_gateway_rest_api.main_api.id}"
  stage_name  = "${aws_api_gateway_deployment.main_api_depoyment.stage_name}"
  domain_name = "${aws_api_gateway_domain_name.main_api_domain_name.domain_name}"
}