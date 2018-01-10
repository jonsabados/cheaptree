data "aws_route53_zone" "domain" {
  name         = "${var.domain_name}."
  private_zone = false
}

data "aws_acm_certificate" "api_cert" {
  domain   = "api.${var.domain_name}"
  statuses = [
    "ISSUED"
  ]
}

data "aws_acm_certificate" "ui_cert" {
  domain   = "${var.domain_name}"
  statuses = [
    "ISSUED"
  ]
}

resource "aws_api_gateway_domain_name" "api_domain_name" {
  domain_name     = "api.${var.domain_name}"

  certificate_arn = "${data.aws_acm_certificate.api_cert.arn}"
}

resource "aws_route53_record" "api_dns_entry" {
  zone_id = "${data.aws_route53_zone.domain.zone_id}"

  name    = "${aws_api_gateway_domain_name.api_domain_name.domain_name}"
  type    = "A"

  alias {
    name                   = "${aws_api_gateway_domain_name.api_domain_name.cloudfront_domain_name}"
    zone_id                = "${aws_api_gateway_domain_name.api_domain_name.cloudfront_zone_id}"
    evaluate_target_health = true
  }
}

resource "aws_api_gateway_base_path_mapping" "treeapi_domain_base_path_to_prod" {
  api_id      = "${var.api_id}"
  stage_name  = "${var.stage_name}"
  domain_name = "${aws_api_gateway_domain_name.api_domain_name.domain_name}"
}
