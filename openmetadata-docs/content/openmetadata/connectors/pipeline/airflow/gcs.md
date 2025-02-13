---
title: Extract GCS Composer Metadata
slug: /openmetadata/connectors/pipeline/airflow/gcs
---

# Extract GCS Composer Metadata

<Note>

This approach has been tested against Airflow 2.1.4. If you have any issues or questions,
please do not hesitate to reach out!

</Note>

The most comfortable way to extract metadata out of GCS Composer is by directly creating a DAG in there
that will handle the connection to the metadata database automatically and push the contents
to your OpenMetadata server.

## Install the Requirements

In your environment you will need to install the following packages:

- `openmetadata-ingestion==x.y.z`, (e.g., `openmetadata-ingestion==0.12.0`).
- `sqlalchemy==1.4.27`: This is needed to align OpenMetadata version with the Composer internal requirements.
- `flask-appbuilder==3.4.5`: Again, this is just an alignment of versions so that `openmetadata-ingestion` can
  work with GCS Composer internals.

<Note>

Make sure to use the `openmetadata-ingestion` version that matches the server version
you currently have!

</Note>

## Prepare the DAG!

Note that this DAG is a usual connector DAG, just using the Airflow service with the `Backend` connection.

As an example of a DAG pushing data to OpenMetadata under Google SSO, we could have:

```python
"""
This DAG can be used directly in your Airflow instance after installing
the `openmetadata-ingestion` package. Its purpose
is to connect to the underlying database, retrieve the information
and push it to OpenMetadata.
"""
from datetime import timedelta

import yaml
from airflow import DAG

try:
    from airflow.operators.python import PythonOperator
except ModuleNotFoundError:
    from airflow.operators.python_operator import PythonOperator

from airflow.utils.dates import days_ago

from metadata.ingestion.api.workflow import Workflow

default_args = {
    "owner": "user_name",
    "email": ["username@org.com"],
    "email_on_failure": False,
    "retries": 3,
    "retry_delay": timedelta(minutes=5),
    "execution_timeout": timedelta(minutes=60),
}

config = """
source:
  type: airflow
  serviceName: airflow_gcs_composer
  serviceConnection:
    config:
      type: Airflow
      hostPort: http://localhost:8080
      numberOfStatus: 10
      connection:
        type: Backend
  sourceConfig:
    config:
      type: PipelineMetadata
sink:
  type: metadata-rest
  config: {}
workflowConfig:
  loggerLevel: INFO
  openMetadataServerConfig:
    hostPort: https://sandbox.getcollate.io/api
    authProvider: google
    securityConfig:
      secretKey: /home/airflow/gcs/data/gcs_creds_beta.json
"""


def metadata_ingestion_workflow():
    workflow_config = yaml.safe_load(config)
    workflow = Workflow.create(workflow_config)
    workflow.execute()
    workflow.raise_from_status()
    workflow.print_status()
    workflow.stop()


with DAG(
    "airflow_metadata_extraction",
    default_args=default_args,
    description="An example DAG which pushes Airflow data to OM",
    start_date=days_ago(1),
    is_paused_upon_creation=True,
    schedule_interval="*/5 * * * *",
    catchup=False,
) as dag:
    ingest_task = PythonOperator(
        task_id="ingest_using_recipe",
        python_callable=metadata_ingestion_workflow,
    )
```

## Google SSO

Against Google SSO we need to use the [Cloud Storage](https://cloud.google.com/composer/docs/concepts/cloud-storage)
to pass the `secretKey` JSON file. Upload the file to the `gs://bucket-name/data` directory, which will be mapped
against `/home/airflow/gcs/data/` in Airflow.

You can see in the example above how our file is named `gcs_creds_beta.json`, which gets resolved in Airflow as
`/home/airflow/gcs/data/gcs_creds_beta.json`.
