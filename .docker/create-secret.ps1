 
$env:LS_HOST = "http://localhost:4566"
$env:PROFILE = "dev"
$env:SECRET = "product_database_credentials"

Write-Host "### Criando Queue(Standard) a secret do database no SecretManager do LocalStack..."

aws --endpoint http://localhost:4566 --profile $env:PROFILE secretsmanager create-secret --name $env:SECRET --description "Exemplo de Secrets Manager" --secret-string '{\"appname\":\"product\",\"host\":\"jdbc:postgresql://localhost:5432/product\",\"user\":\"admin\",\"pass\":\"passW@rd\",\"dialect\":\"org.hibernate.dialect.PostgreSQLDialect\"}'


