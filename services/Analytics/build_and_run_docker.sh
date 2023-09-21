# build project
./gradlew buildFatJar

# clean old images and containers
./gradlew dockerClean
./gradlew dockerRemoveContainer

# build image and start container
# see 'docker ps'
# to stop run 'docker stop analytics' or './gradlew dockerStop'
./gradlew docker
./gradlew dockerRun

# build project
#docker build -t analytics .

# run docker
#docker run -p 50051:50051 -p 50052:50052 my-application