package togos.rdf;

import togos.mf.value.URIRef;

/**
 * Can be used in place of other RDFExpressions to mean
 * 'the thing that this URI references'.  If this is an x-rdf-subject:...
 * URI or otherwise references an object that can be represented with an
 * RDFExpression, this should be considered semantically equivalent to
 * that RDFExpression. 
 * 
 * Note that if you are using RDFExpressions to represent a program,
 * then the referenced resource will also be treated as a program
 * expression (again, as if it were included inline).
 */
public interface RDFURIRef extends RDFExpression, URIRef {}
