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

# base role that lambdas will execute as
resource "aws_iam_role" "lambda_base_execution_role" {
  name               = "lambda-base-execution-role"

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
  name       = "default"
  roles      = [
    "${aws_iam_role.lambda_base_execution_role.name}"
  ]
  policy_arn = "arn:aws:iam::aws:policy/AWSLambdaExecute"
}


# role for the API gateway
resource "aws_iam_role" "api_gateway_role" {
  name               = "api-gateway-role"

  assume_role_policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "",
            "Effect": "Allow",
            "Principal": {
                "Service": "apigateway.amazonaws.com"
            },
            "Action": "sts:AssumeRole"
        }
    ]
}
EOF
}

# give api gateway role permissions to actually create logs
resource "aws_iam_policy_attachment" "api_gateway_role_cloudwatch_policy" {
  name       = "default"
  roles      = [
    "${aws_iam_role.api_gateway_role.name}"]
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
}

resource "aws_api_gateway_account" "gateway_account_settings" {
  cloudwatch_role_arn = "${aws_iam_role.api_gateway_role.arn}"
}

resource "aws_api_gateway_rest_api" "main_api" {
  name        = "CheapTreeAPI"
  description = "REST API for cheap tree"
}


# grant api gateway permission to execute lambda
resource "aws_lambda_permission" "apigw_lambda_hello" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.hello_lambda.arn}"
  principal     = "apigateway.amazonaws.com"

  source_arn    = "arn:aws:execute-api:${var.region}:${var.accountId}:${aws_api_gateway_rest_api.main_api.id}/*/${aws_api_gateway_method.hello_method.http_method}${aws_api_gateway_resource.hello_world_resource.path}"
}

resource "aws_api_gateway_deployment" "main_api_depoyment" {
  depends_on  = [
    "aws_api_gateway_integration.hello_world_resource_integration"
  ]

  rest_api_id = "${aws_api_gateway_rest_api.main_api.id}"
  stage_name  = "prod"
}

resource "aws_api_gateway_method_settings" "api_gateway_logging" {
  rest_api_id = "${aws_api_gateway_rest_api.main_api.id}"
  stage_name  = "prod"
  method_path = "*/*"

  settings {
    metrics_enabled = true
    logging_level   = "INFO"
  }
}

resource "aws_cloudwatch_log_group" "api_gateway_logs" {
  name              = "API-Gateway-Execution-Logs_${aws_api_gateway_rest_api.main_api.id}/prod"
  retention_in_days = 14
}

resource "aws_s3_bucket" "log_bucket" {
  bucket = "logs.cheaptree"
  acl    = "private"
}

resource "aws_s3_bucket" "ui_bucket" {
  bucket = "ui.cheaptree"
  acl    = "public-read"

  policy = <<EOF
{
  "Version":"2012-10-17",
  "Statement":[{
    "Sid":"PublicReadGetObject",
    "Effect":"Allow",
    "Principal": "*",
    "Action":["s3:GetObject"],
    "Resource":["arn:aws:s3:::ui.cheaptree/*"
  ]}
]
}
EOF
}

module "maternal_domain" {
  source          = "domain_name"
  domain_name = "${var.maternal_domain}"
  api_id = "${aws_api_gateway_rest_api.main_api.id}"
  stage_name = "${aws_api_gateway_deployment.main_api_depoyment.stage_name}"
  log_bucket_name = "${aws_s3_bucket.log_bucket.bucket_domain_name}"
  ui_bucket_domain_name = "${aws_s3_bucket.ui_bucket.bucket_domain_name}"
}

module "paternal_domain" {
  source          = "domain_name"
  domain_name = "${var.paternal_domain}"
  api_id = "${aws_api_gateway_rest_api.main_api.id}"
  stage_name = "${aws_api_gateway_deployment.main_api_depoyment.stage_name}"
  log_bucket_name = "${aws_s3_bucket.log_bucket.bucket_domain_name}"
  ui_bucket_domain_name = "${aws_s3_bucket.ui_bucket.bucket_domain_name}"
}
