# Changes between TNL v1 and v3

- Syntax is more strictly defined.  No trailing commas or semicolons are
  allowed.
- Places that used to accept expressions referencing x, y, z now take
  3-argument functions.  There are no longer 'magic' x, y, z variables.
- Functions must be explicitly called.  References to functions without
  argument lists evaluate to the functions themselves.  e.g. 'simplex' and
  'simplex(1,2,3)' resolve to very different variables.
- Expressions are more strongly typed.  Trying to use a number where a
  true/false is expected will result in an error, and vice-versa; you
  need to convert explicitly using comparison operators and if(...) 
- Lots of built-in functions have been removed, as it is now more feasible
  to implement them in script code.
- User-defined functions can take named arguments.

While these changes may make it slightly more difficult to write scripts,
they also allow the compiler to catch mistakes earlier and compile your
script to a more efficient internal representation, and they simplify
the implementation of the compiler, making it easier to extend (though
the compiler may be more complex in other ways, those complexities are
not dictated by the language).
