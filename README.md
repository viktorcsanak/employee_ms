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

