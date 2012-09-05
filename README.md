# Cellmate: Streamlined BigTable read/write/search #

Framework to help abstract columnar data collection and manipulation from the details specific to that API.  

Users manipulate read/write from cellmate using collections of cells stored in one or more tuples. DAO layers can be architected with respect to these tuples and cells, primarily decoupled from the underlying BigTable semantics. Users can use the prebuilt API specific readers/writers and transformers, or implement their own to support custom cell types. 

## This API provides.. ##

1) Reader implementations for HBase and Accumulo that support any cell type transform operation. (Note: Only basic ops supported 	   	at this time. Nothing fancy yet.)
2) Concrete cell implementations to support HBase and Accumulo.  
3) Concrete cell transformer examples to support the prebuilt cell types over the Accumulo/HBase readers.
4) Writer implementations for HBase and Accumulo that automatically handle persisting any custom cell type. 
5) Concrete cell extractors to handle parsing cells by label and custom fields. 
6) Annotations and interfaces to help build custom cells and transformers with use in the various readers. 

## Not yet ready ##

None of the HBase reader/writer code is implemented yet. The Accumulo code is checked in, but not yet unit tested. 

## Usage ##
Licensed AS-IS under Apache License 2.0



