# interparliadataset
Inter-Parliamentary Dataset

by Edmundo Andrade (edworld) 2018

## Compile from source
> cd tool

> mvn clean package

As a result, target/interparliadataset-0.0.1-SNAPSHOT.jar will be generated. 

## Execute to import one or more European Union's laws in all languages available
> java -jar interparliadataset-0.0.1-SNAPSHOT.jar --CELEX 32017R2403 --CELEX 31974B0144

By default, the blocks of text will be imported and aligned according to the available languages:

BG-ES-CS-DA-DE-ET-EL-EN-FR-GA-HR-IT-LV-LT-HU-MT-NL-PL-PT-RO-SK-SL-FI-SV.

As result, it will be create/updated two files:

* dataset/metadata.csv

* dataset/textblocks.csv

If the files already exist, the imported content will be appended to them, preserving previous imported data. 

## Execute to import one or more European Union's laws in the specified languages, as available
> java -jar interparliadataset-0.0.1-SNAPSHOT.jar --languages EN-PT-ES-FR --CELEX 31975B0136

