# Talend Components for reading SAP data via RFC
Talend Component working with SAP RFC calls to the default function RFC_READ_TABLE

## tSAPRFCConnection
This component established the connection.

Direct connetion with the shipped sapjco3.jar
It can connect directly to the SAP server. This requires the component (and therefore the job must carry the sapjco3.jar library.
This library requires the installation of the native library and the path to this lib must be referenced with the environment library JNI_LIBRARY_PATH

Connect to the Proxy-SAP-Server
This server hats the advantage the sapjco3.jar must only by installed for this service and the amount of jobs don't.
The service is described and published here: https://github.com/jlolling/SAPRFCProxyServer

As URL for the service http://your-server:9999

These 2 ways are configured with the option Connection Type.

A typical job design:
![Job here the connection components](https://github.com/jlolling/talendcomp_tSAPRFC/blob/master/doc/tSAPRFC_real_live_job_design.png)

## tSAPRFCTableInput

Runs the query.
The component expects mandatory the name of the table.
You can setup a filter.
Filter extressions are actually limited by 72 chars. You can configure much longer expressions. 
To allow the component to cut the expressions in 72 or less pieces you have to add seimcolon and the component will cut the expressions there.

![Job here the input component](https://github.com/jlolling/talendcomp_tSAPRFC/blob/master/doc/tSAPRFCTableInput_real_live_job_design.png)

![Job here the input component schema](https://github.com/jlolling/talendcomp_tSAPRFC/blob/master/doc/tSAPRFCTableInput_schema.png)

If the actual query have fields with / or spaces in such case set the actual database column as database column and rename it for the schema 


