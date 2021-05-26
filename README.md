# Projeto conversor de moedas

Este projeto é uma API de conversão de moedas, onde o usuário, fornecendo um Id, as moedas de origem e destino da conversão, e o valor a ser convertido, obtém o valor convertido, utilizando a cotação do dia (com base na API https://currencylayer.com/).

## Tecnologias utilizadas

Este projeto utiliza Quarkus. Sua escolha recai principalmente pela facilidade de uso na criação de aplicações rebuscadas, além de ter uma boa curva de aprendizagem.
Se você deseja aprender mais sobre o Quarkus, visite https://quarkus.io/ .

Associado ao Quarkus, foram utilizados:
- OpenAPI (Swagger): para geração de documentação da API. Utiizado pela facilidade de uso e configuração (principalmente pelo uso de anotações);
- H2: banco de dados (embedded);

## Camadas da aplicação

O projeto possui duas camadas: a principal, que contêm as classes _Resource_ , responsáveis pelos endpoints, e a _Service_ que cuida da persistência do histórico de conversão.

## Utilizando a API

A API possui dois endpoints
###### /currencyConverter/convert/{IdUsuario}/{MoedaOrigem}/{MoedaDestino}/{ValorParaConversao}
Retorno: valor da conversão entre duas moedas;
Onde:
- IdUsuario: identificador do usuário utilizando a API, no momento da consulta;
- MoedaOrigem: sigla da moeda (p. ex: Real Brasileiro é BRL, Dólar Americano é USD) que é a origem do valor para conversão;
- MoedaDestino: sigla da moeda de destino da conversão;
- ValorParaConversao;

###### /conversionLog/getByIdUser/{IdUsuario}
Retorno: lista das conversões realizadas por Id;
Onde:
- IdUsuario: identificador do usuário utilizando a API

Para maiores informações e possibilidade de teste, uma vez a aplicação sendo executada, é possível acessar o frontend do Swagger para visualizar a documentação em /q/swagger-ui/

## Executando a aplicação no modo dev

```shell script
./mvnw compile quarkus:dev
```

## Empacotando e executando a aplicação

```shell script
./mvnw package
```
Este comando produz um arquivo `quarkus-run.jar` no diretório `target/quarkus-app/`.
O arquivo gerado não é um  _über-jar_  e suas dependências estarão todas em `target/quarkus-app/lib/`.

Se você deseja um  _über-jar_ , execute o seguinte comando:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

A aplicação agora é executável através do comando `java -jar target/quarkus-app/quarkus-run.jar`.

## Criando um executável nativo

O Quarkus permite a criação de executáveis nativos, com tempos de execução e performance superiores aos _über-jar_. Para saber como criar executáveis nativos, favor acessar a documentação do Quarkus