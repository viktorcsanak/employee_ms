Mean stack setup on Fedora 39.

sudo dnf update

sudo dnf install nodejs

sudo tee /etc/yum.repos.d/mongodb.repo << "EOF" > /dev/null
[mongodb]
name=MongoDB
baseurl=https://repo.mongodb.org/yum/redhat/9/mongodb-org/7.0/x86_64/
gpgkey=https://www.mongodb.org/static/pgp/server-7.0.asc
EOF
sudo rpm --import https://www.mongodb.org/static/pgp/server-7.0.asc
sudo dnf install mongodb-org
sudo systemctl enable mongod.service
sudo systemctl restart mongod.service

sudo npm install -g @angular/cli

Express
npm init -y
npm install express --save

mongodump --out=/tmp/mongodump

# Spring-boot(Debian 12)

## Local

To run the application with a local kafka and postgres setup run ` mvn clean install -Dspring.profiles.active=local` and `mvn spring-boot:run`

## Docker 
To run the application in containers, build it with `mvn clean install -DskipTest` (the tests expect kafka and postgres at localhost, so they will fail with the docker environment parameters) and the build the containers and run them with `docker compose up --build`

NOTE that if you already started kafka or postgres locally you will have to stop them, since for now the ports are colliding.
