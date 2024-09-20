# sandbox-java-spring-rabbitmq-s3


# Instruções para Executar o Devcontainer e Testar a Aplicação

![Image](docs/Screenshot%202024-09-20%20074716.png)

## Pré-requisitos

1. Certifique-se de ter o Docker e o Docker Compose instalados em sua máquina.
2. Tenha o Visual Studio Code com a extensão "Remote - Containers" instalada.

## Como Executar o Devcontainer

Siga os passos abaixo para iniciar o ambiente de desenvolvimento utilizando Devcontainer:

1. Clone este repositório na sua máquina local:
   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd <PASTA_DO_REPOSITORIO>
   ```

2. Abra o projeto no Visual Studio Code:
   ```bash
   code .
   ```

3. Quando o Visual Studio Code abrir o projeto, ele deve detectar automaticamente o arquivo `.devcontainer` e oferecer a opção de "Reabrir no Dev Container". Se isso não acontecer, você pode abrir manualmente:
   - Clique no ícone do Docker na barra inferior esquerda do VS Code.
   - Selecione "Reabrir no Dev Container".

4. O VS Code abrirá o ambiente dentro do Devcontainer e instalará automaticamente todas as dependências necessárias.

## Explicação dos Serviços no Docker Compose

O arquivo `docker-compose.yaml` define diversos serviços que são executados dentro do Devcontainer. Aqui está uma breve explicação de cada um deles:

- **sandbox-sinqia-rabbitmq**: Este serviço executa o RabbitMQ, que é responsável pela fila de mensagens para o processamento de imagens coloridas e em preto e branco.
  - Exposto em: `localhost:15672` para a interface de gerenciamento.

- **sandbox-sinqia-springboot**: Serviço que executa a aplicação Spring Boot, responsável por baixar as imagens da web, enviar para as filas do RabbitMQ, e fazer o processamento de imagens.
  - Exposto em: `localhost:8080` para a API da aplicação.

- **sandbox-sinqia-python-worker**: Este serviço é responsável por consumir as mensagens da fila RabbitMQ e salvar as imagens processadas no diretório local.
  - Conecta-se ao RabbitMQ para processar as mensagens.

- **sandbox-sinqia-minio**: Serviço que simula o Amazon S3 localmente. Ele armazena as imagens processadas e pode ser acessado via:
  - Exposto em: `localhost:9001` para o console do MinIO.

- **sandbox-sinqia-awscli**: CLI da AWS para interagir com o serviço MinIO e realizar operações em seus buckets e objetos.

## Como Testar a Aplicação via Curl

Você pode testar o processo de download e processamento de imagens utilizando o comando `curl`. Para isso, siga os passos abaixo:

1. Execute o seguinte comando para baixar 100 imagens da web, processá-las e salvá-las nos diretórios monitorados (`img/colored` e `img/bw`):
   ```bash
   curl http://localhost:8080/images/fetch-and-save
   ```

2. Este comando fará com que as imagens sejam baixadas e enviadas para o RabbitMQ. O `DirectoryWatcherService` ficará responsável por monitorar os diretórios e, em seguida, enviar os arquivos para o MinIO.

3. Verifique os logs para garantir que as imagens foram processadas corretamente:
   ```bash
   docker-compose logs -f
   ```

4. Após o processamento, você pode acessar o console do MinIO para visualizar as imagens processadas:
   ```
   http://localhost:9001
   ```

5. Tambem será possivel ver as imagens na pasta img, divididas entre coloridas e preto&brancas

## Encerrando

Quando terminar de usar o Devcontainer, você pode parar os serviços com:
```bash
docker-compose down
```
