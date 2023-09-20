# build project
./gradlew build

# clean old images and containers
./gradlew dockerClean
./gradlew dockerRemoveContainer

# build image and start container
# see 'docker ps'
# to stop run 'docker stop requeuer' or './gradlew dockerStop'
./gradlew docker
./gradlew dockerRun