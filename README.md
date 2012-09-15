# Cellmate #

## Data abstraction over Accumulo ##

The cellmate API lets you build domain objects and DAO layers around the cell group interface, instead of the raw Accumulo Key/Value pairs. The net result is less biolerplate setup and strong decoupling between your domain objects and Accumulo operations.

Users can focus on their business logic and solving problems, and less on the Accumulo API details of setting up scans, connections, authorizations, filters, and other requirements. All of that is handled for you by Cellmate. 

## What is Cellmate? ##

Cellmate core contains a number of discrete components that when combined help abstract Accumulo operations. 

Cell: label/value pairs that typically mark a single string or byte[] value with some meaningful context (usually the qualifier from the Key/Value pair). Users can use one of the prebuilt cell types, or implement their own with annotations. 
CellGroup: collection of cells with a tag (usually the rowId from the Key/Value pair).
Parameters: Lets you supply common query parameters required for Accumulo scan (user,pass, start-key, end-key, iterators, etc)
CellTransformer: Function that tells the reader how to take Key/Value pairs and build Cells and Cell groups from the scanning results.
Reader: Once you define your parameters, you can call read() and get back transformed cells. 
Extractor: Once you have collections of CellGroups and Cells, the extractors help you dereference the cell label/value pairs based on filter patterns or direct lookup on known labels. 
Writer: Given one or more CellGroup(s), the writer will generate the correct Mutations and persist back to Accumulo. (Note: At this time, this has functionally has not be tested.)

## What this API provides ##

<ol>
<li>Reader implementations Accumulo that enable rapid development of read/write applications and filtered queries. 
<li>Various prebuilt cell classes for use over Accumulo that cover many use cases. </li>
<li>Several Accumulo cell transformer classes that cover many common use cases. </li>
<li>Writer implementations for Accumulo that automatically handle persisting any custom cell type.</li>
<li>Cell extractors that assist with parsing cells and cell groups for use with your higher-level domain objects.</li>
<li>Annotations and interfaces to help build custom cells, transformers or reader/writer implementations.</li>
</ol>

## Examples ##

Checkout this project wiki for Getting Started and some basic tutorials.  

## Usage ##
Licensed AS-IS under Apache License 2.0



