# Cellmate #

## DAO over BigTable made easy ##
This framework helps abstract columnar data collection and manipulation from the raw datastore APIs.  

Cellmate provides classes that abstract the datastore key/value pairs into collections of cells stored in one or more cell groups. DAO layers can be architected with respect to these cells, primarily decoupled from the underlying BigTable semantics. Users can write inline functional code for custom cell transformation, or use some of the prebuilt transformer classes to familiarize themselves with the approach.  

The cell group concept is highly applicable to the semi-structured nature of a BigTable data model. Cellmate presents a different approach that is more open ended, flexible, and intuitive than traditional ORM abstractions. Its cell group approach lets you think about your data in domain specific ways, while being careful to not force any schema constraints as you query from or persist to tables. Additionally, the reader/writer classes eliminate much of the boilerplate code associated with common CRUD and searching operations. 
## What this API will provide when complete ##

<ol>
<li>Reader implementations for HBase and Accumulo that enable rapid development of CRUD and filtered queries. 
<li>Various prebuilt cell classes for use over HBase/Accumulo that cover many use cases. </li>
<li>Several HBase and Accumulo cell transformer examples that use the prebuilt cell classes to cover many common operations. </li>
<li>Writer implementations for HBase and Accumulo that automatically handle persisting any custom cell type.</li>
<li>Cell extractors that assist with parsing cells and cell groups for use with your higher-level domain objects.</li>
<li>Annotations and interfaces to help build custom cells, transformers or reader/writer implementations.</li>
<li>DAO examples in both HBase and Accumulo to demonstrate how effective Cellmate is for Bigtable DAO abstraction.</li>
<li>Search criteria API that automatically generates filtered queries and cell group results.  
</ol>

## Not yet ready ##

The codebase is only about 30% complete with the above listed requirements in mind. Hang in there.  

## Usage ##
Licensed AS-IS under Apache License 2.0



