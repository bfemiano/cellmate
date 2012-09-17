# Cellmate #

<p><a href="https://github.com/bfemiano/cellmate/wiki">Getting Started</a></p>
<p><a href="https://cellmate-docs.s3.amazonaws.com/javadoc/index.html">Javadocs API </a></p>

## Data abstraction over Apache Accumulo(http://accumulo.apache.org/) ##

The Cellmate API lets you build domain objects and DAO layers over its simple and concise cell API, instead of the raw Accumulo Key/Value pairs. The net result is less biolerplate and strong decoupling between your domain objects and Accumulo operations.

Users can focus on their business logic and solving problems, and less on the Accumulo API details of setting up scans, connections, authorizations, filters, and other requirements. All of that is handled for you by Cellmate. 


## What is Cellmate? ##

The core library contains a number of discrete components that help abstract Accumulo operations. 
<ol>
<li><b>Cells</b> that typically mark a single string or byte[] value with some meaningful label (usually the qualifier from the Key/Value pair). Users can use one of the prebuilt cell types, or implement their own with annotations.</li> 
<li><b>CellGroup</b> collections that mark cells with a tag (usually the rowId from the Key/Value pair).</li>
<li><b>Parameters</b> that let users define required scan/write options for Accumulo scan and writes (user, password, start-key, end-key, iterators, etc).</li>
<li><b>Reader</b> to take parameters and return scanned cell groups.</li> 
<li><b>CellTransformer</b> functions that tells the reader how to take Key/Value pairs and build cells and cell groups from the scan results.</li>
<li><b>Extractor</b> that supports dereferencing data in cells based on filter patterns or direct lookup on known labels.</li> 
<li><b>Writer</b> to take cell groups and write Mutations back to Accumulo. (Note: At this time, this has functionally has not be tested.)</li>
</ol>

## What this API provides ##

<ol>
<li>Reader implementations Accumulo that enable rapid development of read/write applications and filtered queries. 
<li>Various prebuilt cell classes for use over Accumulo that cover many use cases. </li>
<li>Several Accumulo cell transformer classes that cover many common use cases. </li>
<li>Writer implementations for Accumulo that automatically handle persisting any custom cell type.</li>
<li>Cell extractors that assist with parsing cells and cell groups for use with your higher-level domain objects.</li>
<li>Annotations and interfaces to help build custom cells, transformers or reader/writer implementations.</li>
</ol> 

## Usage ##
Licensed AS-IS under Apache License 2.0

## Apache Accumulo ##
<a href="www.http://accumulo.apache.org/">http://accumulo.apache.org/</a>



