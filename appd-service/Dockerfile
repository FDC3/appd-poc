FROM java:8


# Install the AWS CLI
RUN apt-get update && apt-get -y install python curl unzip && cd /tmp && curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip" && unzip awscli-bundle.zip && ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws && rm awscli-bundle.zip && rm -rf awscli-bundle


# Install the new entry-point script
# Requires BUCKET_NAME env, which should be S3 bucket+prefix eg. bucket/central-configs/apps/vote-vot/global/esco-bot
# Set env during docker run
COPY env-entrypoint.sh /env-entrypoint.sh


ADD target/appd-service/appd-service-0.0.1/ /opt/appd-service
ADD target/appd-service-classes.jar /opt/appd-service/lib
EXPOSE 8080

ENTRYPOINT ["/env-entrypoint.sh"]
CMD ["/opt/appd-service/bin/AppD"]
