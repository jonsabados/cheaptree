Assumption: you are deploying to a domain with dns managed by route53

Deployment:

install terraform (`brew install terraform` on osx)

```
export AWS_ACCESS_KEY_ID=${YOURACCESSKEYHERE}
export AWS_SECRET_ACCESS_KEY=${YOURSECRETACCESSKEYHERE}
```

build the code: `mvn package`

request a cert for treeapi.WHATEVERDOMAIN via ACM

deploy to aws (you should probably run a plan first):
`terraform apply`

Terraform will prompt for the account ID and domain name, set the following environmental variables to get around this: 
* TF_VAR_accountId
* TF_VAR_domain
