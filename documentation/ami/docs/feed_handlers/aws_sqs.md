# AWS SQS

## Configuration

1.  In order to set up the SQS feedhandler, unzip the tar.gz or zip file and copy the contents of the folder into the `amione/lib` directory.
2.  For generating AWS credentials, two methods are supported - (1) via **STS**, or (2) via **Profiles**.
    1.  **STS**: Ensure that both `ami.relay.fh.sqs.props.roleArn` and `ami.relay.fh.sqs.props.roleSessionName` properties are set. If either is unset, the feed handler will default to using profiles. Ensure that you have a role with read access to the relevant AWS SQS queue. Note: this method will use your default credentials to assume the provided role (see [AWS Default Credentials Provider](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/auth/credentials/DefaultCredentialsProvider.html)) for more information on how this is acquired)
    2.  **Profiles**: Ensure that you have a valid AWS credential tagged to a profile configured with read access to the relevant AWS SQS queue, see Step 2 of: [AWS SQS Setting Up](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-setting-up.html) For configuring your profile for the first time, run `aws configure sso` and pass in the requested information to generate the required files and credentials for the SDK. Subsequently, running `aws sso login --profile profilename` will refresh your credentials.
3.  Add the respective properties to your local.properties file according to the section below

### Message Format

This feedhandler accepts only valid JSON as an input source from SQS and expects it to be in a single map where each key represents a column on a table and its value a single column value.

Sample valid message:

```
{
  "colA": "colAValue",
  "colB": 123,
  ...
}
```

Note that by default, all column types are inferred to be Strings unless explicitly defined In the `ami.relay.fh.sqs.props.tableMapping` property using a `colName=colType,...` syntax.

Null values in the JSON message are skipped.

Valid types are as follows:

| Underlying Column Type | Valid Property Name (Case Insensitive) |
|------------------------|----------------------------------------|
| String                 | `str`, `string`                            |
| Integer                | `int`, `integer`                           |
| Short                  | `short`                                  |
| Long                   | `long`                                   |
| Float                  | `float`                                  |
| Double                 | `double`                                 |
| Character              | `char`, `character`                        |
| Boolean                | `bool`, `boolean`                          |

## Properties

```
# Required - use this to configure one or more FHs
ami.relay.fh.active=sqs

# Required - used to start the FH
ami.relay.fh.sqs.start=true

# Required - must match exactly
ami.relay.fh.sqs.class=com.f1.ami.relay.fh.sqs.AmiAwsSqsFH

# Required - name of AMI table for data to be streamed into
ami.relay.fh.sqs.props.tableName=tableName

# Optional - AWS profile name, uses default profile otherwise
ami.relay.fh.sqs.props.profileName=AdministratorAccess-123456

# Optional - AWS profile file, uses default AWS location otherwise
ami.relay.fh.sqs.props.profileFile=/location/to/file

# Optional - AWS role ARN
ami.relay.fh.sqs.props.roleArn=arn

# Optional - AWS role session name
ami.relay.fh.sqs.props.roleSessionName=sessionName

# Required - URL of the SQS Queue
ami.relay.fh.sqs.props.queueUrl=https://sqs.us-east-1.amazonaws.com/123/queueName

# Required - AWS region of the SQS Queue
ami.relay.fh.sqs.props.queueRegion=us-east-1

# Optional - If true, deletes read messages from the queue
ami.relay.fh.sqs.props.deleteAfterRead=true

# Optional - Specify column name and its underlying types, see Message Format section above for more info
ami.relay.fh.sqs.props.tableMapping=colA=String,colB=int

# Optional - Number of messages to read at a time (defaults to 5)
ami.relay.fh.sqs.props.readCount=5
```

