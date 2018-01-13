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

resource "aws_cloudfront_distribution" "ui_distribution" {
  origin {
    domain_name = "${var.ui_bucket_domain_name}"
    origin_id   = "s3Origin${var.domain_name}"
  }

  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"

  logging_config {
    include_cookies = false
    bucket          = "${var.log_bucket_name}"
    prefix          = "${var.domain_name}"
  }

  aliases             = [
    "${var.domain_name}"
  ]

  default_cache_behavior {
    allowed_methods        = [
      "GET",
      "HEAD",
      "OPTIONS"
    ]
    cached_methods         = [
      "GET",
      "HEAD"
    ]
    target_origin_id       = "s3Origin${var.domain_name}"

    forwarded_values {
      query_string = false

      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "allow-all"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
  }

  price_class         = "PriceClass_100"

  restrictions {
    geo_restriction {
      restriction_type = "whitelist"
      locations        = [
        "US"
      ]
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = false
    acm_certificate_arn            = "${data.aws_acm_certificate.ui_cert.arn}"
    ssl_support_method             = "sni-only"
  }
}

resource "aws_route53_record" "ui_dns_entry" {
  zone_id = "${data.aws_route53_zone.domain.zone_id}"

  name    = "${var.domain_name}"
  type    = "A"

  alias {
    name                   = "${aws_cloudfront_distribution.ui_distribution.domain_name}"
    zone_id                = "${aws_cloudfront_distribution.ui_distribution.hosted_zone_id}"
    evaluate_target_health = true
  }
}