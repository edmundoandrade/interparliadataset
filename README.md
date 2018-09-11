# interparliadataset
Inter-Parliamentary Dataset

by Edmundo Andrade (edworld) 2018

## Compile from source
> cd tool

> mvn clean package

As a result, target/interparliadataset-0.0.1-SNAPSHOT.jar will be generated. 

## Execute to import one or more European Union's laws in all languages available
> java -jar interparliadataset-0.0.1-SNAPSHOT.jar --CELEX:32017R2403 --CELEX:31974B0144

This tool is able to download and convert legislative data given:

* the CELEX number of one or more European Union's laws; for instance:

** --CELEX:32017R2403 --CELEX:31974B0144

* - the LEX URN of one or more Brazil's laws; for instance:

** --urn:lex:br:federal:decreto:1972-07-28;70885

By default, European Union's laws will be downloaded in the available EUR-Lex languages:

* BG+ES+CS+DA+DE+ET+EL+EN+FR+GA+HR+IT+LV+LT+HU+MT+NL+PL+PT+RO+SK+SL+FI+SV

To restrict these languages, use this model: --languages EN+PT

In case of Brazil's laws, there is only one target language: PT-BR

As result, the legislative data will be saved in two files

* dataset/metadata.csv

* dataset/text.csv

New data will always be appended to the end of these files in case they already exist.

## Execute to import one or more European Union's laws in the specified languages, as available
> java -jar interparliadataset-0.0.1-SNAPSHOT.jar --languages EN+PT+ES+FR --CELEX:31975B0136

## Parameters needed when executing behind proxy
> java -Dhttp.proxyHost=localhost -Dhttp.proxyPort=3128 -Dhttps.proxyHost=localhost -Dhttps.proxyPort=3128 -jar interparliadataset-0.0.1-SNAPSHOT.jar --CELEX:32017R2403 --CELEX:31974B0144
