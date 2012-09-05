# Cellmate #

## Streamlined BigTable operations ##
This framework helps abstract columnar data collection and manipulation from datastore API specifics.  

Cellmate provides classes to help abstract the datastore key/value pairs into collections of cells stored in one or more tuples. DAO layers can be architected with respect to these tuples and cells, primarily decoupled from the underlying BigTable semantics. Users can use the prebuilt API specific readers/writers and transformers, or implement their own to support custom cell types. 

## What this API provides ##

<ol>
<li>Reader implementations for HBase and Accumulo that support any cell type transform operation. (Note: Only basic ops supported 	   	at this time. Nothing fancy yet.)</li>
<li>Various prebuilt cell classes for use over HBase/Accumulo that cover many use cases. </li>
<li>Several Accumulo/HBase cell transformer examples to support the prebuilt cell types.</li>
<li>Writer implementations for HBase and Accumulo that automatically handle persisting any custom cell type.</li>
<li>Cell extractors that assist with parsing cells and tuples for higher-level domain objects.</li>
<li>Annotations and interfaces to help build custom cells, transformers or reader/writer implementations.</li>
</ol>

## Not yet ready ##

None of the HBase reader/writer code is implemented yet. The Accumulo reader code is checked in, but not yet unit tested. 

The codebase is about 40% complete. 

## Usage ##
Licensed AS-IS under Apache License 2.0



