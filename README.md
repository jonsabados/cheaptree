Assumption: you are deploying to both a paternal and maternal domain with dns managed by route53 (same app with aliases)

Deployment:

install terraform (`brew install terraform` on osx)

```
export AWS_ACCESS_KEY_ID=${YOURACCESSKEYHERE}
export AWS_SECRET_ACCESS_KEY=${YOURSECRETACCESSKEYHERE}
```

build the code: `mvn package`

request a cert for ${MATERNAL_DOMAIN} via ACM
request a cert for api.${MATERNAL_DOMAIN} via ACM
request a cert for ${PATERNAL_DOMAIN} via ACM
request a cert for api.${PATERNAL_DOMAIN} via ACM

deploy to aws (you should probably run a plan first):
`cde aws && terraform apply`

Terraform will prompt for the account ID and domain names, set the following environmental variables to get around this: 
* TF_VAR_accountId
* TF_VAR_maternal_domain
* TF_VAR_paternal_domain
