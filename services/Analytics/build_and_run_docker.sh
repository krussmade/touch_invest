# build project
./gradlew build

# build image and start container
# see 'docker ps'
# to stop run 'docker stop requeuer' or './gradlew dockerStop'
./gradlew buildImage
./gradlew runDocker