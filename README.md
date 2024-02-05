# Talend Components for reading SAP data via RFC
Talend Component working with SAP RFC calls to the default function RFC_READ_TABLE

## tSAPRFCConnection
This component established the connection. There are to different ways to connect to SAP (configured with the setting Connection Type):

### Connect with the shipped sapjco3.jar driver (deprecated)
It can connect directly to the SAP server. This requires the component (and therefore the job must carry the sapjco3.jar library.
This library requires the installation of the native library and the path to this lib must be referenced with the environment library JNI_LIBRARY_PATH

### Connect via the Proxy-SAP-Server
This server has the advantage the sapjco3.jar must only by installed (along with the native lib) within this service and not in the amount of jobs.
The service is described and published here: https://github.com/jlolling/SAPRFCProxyServer

As URL for the service http://your-server:9999


### A typical job design (regardless of the connection type)
![Job here the connection components](https://github.com/jlolling/talendcomp_tSAPRFC/blob/master/doc/tSAPRFC_real_live_job_design.png)

## tSAPRFCTableInput

Runs the query.
The component expects mandatory the name of the table.
You can setup a filter.
Filter extressions are actually limited by 72 chars. You can configure much longer expressions. 
To allow the component to cut the expressions in 72 or less pieces you have to add semicolon and the component will cut the expressions here.

![Job here the input component](https://github.com/jlolling/talendcomp_tSAPRFC/blob/master/doc/tSAPRFCTableInput_real_live_job_design.png)
Please note the semicolons in the filter expression

![Job here the input component schema](https://github.com/jlolling/talendcomp_tSAPRFC/blob/master/doc/tSAPRFCTableInput_schema.png)

If the actual query have fields with / or spaces in such case set the actual database column as database column and rename it for the schema

Note: In case of there are exceptions caused by a missing CFLF. It turns out, a change of the port helps because e.g. the MySQL driver use this port as internal port for communication.


