resource "aws_s3_bucket" "ui_bucket" {
  bucket = "${var.domain_name}"
  acl    = "public-read"

  policy = <<EOF
{
  "Version":"2012-10-17",
  "Statement":[{
    "Sid":"PublicReadGetObject",
    "Effect":"Allow",
    "Principal": "*",
    "Action":["s3:GetObject"],
    "Resource":["arn:aws:s3:::${var.domain_name}/*"
  ]}
]
}
EOF
}

resource "aws_cloudfront_distribution" "ui_distribution" {
  origin {
    domain_name = "${aws_s3_bucket.ui_bucket.bucket_domain_name}"
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

resource "aws_s3_bucket_object" "object" {
  bucket       = "${aws_s3_bucket.ui_bucket.bucket}"
  key          = "index.html"
  source       = "../ui/index.html"
  etag         = "${md5(file("../ui/index.html"))}"
  content_type = "text/html"
}

