resource "aws_cloudwatch_log_group" "cloudwatch_log_group" {
  name              = "/aws/lambda/${var.resource_name}"
  retention_in_days = 14
}

resource "aws_lambda_function" "lambda_function" {
  filename         = "${var.path_to_jar}"
  function_name    = "${var.resource_name}"
  role             = "${var.execution_role}"
  handler          = "${var.handler_class}"
  source_code_hash = "${base64sha256(file("${var.path_to_jar}"))}"
  runtime          = "java8"

  environment {
    variables {
      ALLOWED_CORS_DOMAINS = "${var.allowed_cors_domains}"
    }
  }
}

# grant api gateway permission to execute lambda
resource "aws_lambda_permission" "apigw_lambda_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.lambda_function.arn}"
  principal     = "apigateway.amazonaws.com"

  source_arn    = "arn:aws:execute-api:${var.region}:${var.account_id}:${var.main_api_id}/*/${aws_api_gateway_method.gateway_method.http_method}${aws_api_gateway_resource.gateway_resource.path}"
}

resource "aws_lambda_permission" "apigw_cors_lambda_permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = "${var.cors_lambda_arn}"
  principal     = "apigateway.amazonaws.com"

  source_arn    = "arn:aws:execute-api:${var.region}:${var.account_id}:${var.main_api_id}/*/${aws_api_gateway_method.gateway_method_cors.http_method}${aws_api_gateway_resource.gateway_resource.path}"
}

resource "aws_api_gateway_resource" "gateway_resource" {
  rest_api_id = "${var.main_api_id}"
  parent_id   = "${var.parent_resource_id}"
  path_part   = "hello"
}

resource "aws_api_gateway_method" "gateway_method" {
  rest_api_id   = "${var.main_api_id}"
  resource_id   = "${aws_api_gateway_resource.gateway_resource.id}"
  http_method   = "${var.http_method}"
  authorization = "CUSTOM"
  authorizer_id = "${var.authorizer_id}"
}

resource "aws_api_gateway_method" "gateway_method_cors" {
  rest_api_id   = "${var.main_api_id}"
  resource_id   = "${aws_api_gateway_resource.gateway_resource.id}"
  http_method   = "OPTIONS"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "gateway_resource_integration" {
  rest_api_id             = "${var.main_api_id}"
  resource_id             = "${aws_api_gateway_resource.gateway_resource.id}"
  http_method             = "${var.http_method}"
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${aws_lambda_function.lambda_function.arn}/invocations"
}

resource "aws_api_gateway_integration" "gateway_cors_resource_integration" {
  rest_api_id             = "${var.main_api_id}"
  resource_id             = "${aws_api_gateway_resource.gateway_resource.id}"
  http_method             = "OPTIONS"
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${var.cors_lambda_arn}/invocations"
}