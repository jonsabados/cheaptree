resource "aws_cloudwatch_log_group" "hello_lambda_logs" {
  name              = "/aws/lambda/hello_world"
  retention_in_days = 14
}

resource "aws_lambda_function" "hello_lambda" {
  filename         = "../backend/hello/target/hello-${var.version}.jar"
  function_name    = "hello_world"
  role             = "${aws_iam_role.lambda_base_execution_role.arn}"
  handler          = "com.jshnd.cheaptree.hello.HelloLambda"
  source_code_hash = "${base64sha256(file("../backend/hello/target/hello-${var.version}.jar"))}"
  runtime          = "java8"
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

# map API resource to lambda
resource "aws_api_gateway_integration" "hello_world_resource_integration" {
  rest_api_id             = "${aws_api_gateway_rest_api.main_api.id}"
  resource_id             = "${aws_api_gateway_resource.hello_world_resource.id}"
  http_method             = "${aws_api_gateway_method.hello_method.http_method}"
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${aws_lambda_function.hello_lambda.arn}/invocations"
}