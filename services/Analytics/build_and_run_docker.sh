# build project
docker build -t analytics .

# run docker
docker run -p 50051:50051 -p 50052:50052 my-application