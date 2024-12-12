# BDSL Core Interpretation Logic

This submodule contains some essential building blocks to interpret BDSL statements.
The architecture emphasizes on extensibility for future grammar extensions that are based on the base BDSL grammar
(see BDSL-CE repository).

**Some Features**
- Interpretation behavior can be customized.
    - The BDSL grammar elements are extended with an `interpret(*)` method (non-invasive) through the extension method feature of lombok. This can easily be converted to the Xtend language
- Results can be cached locally

### Default procedure to interpret a BDSL statement

- Call extension method of the BDSL statement and pass a suitable visitor, possibly just called `interpret(Visitor)`.

- Visitors should internally do the same, meaning, call extension method and pass a visitor implementation.

- Caching is handled outside the visitor, possible in the extension methods if needed.
    - See `org.bigraphs.dsl.variables.extensions.interpreter.BigraphExpressionVisitableExtension`
    as an example, where the local bigraph declarations are cached.
