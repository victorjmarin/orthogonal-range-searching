### Project title
Database Queries as an Orthogonal Range Searching Problem 

### Project description
Database queries typically include constraints over the attributes of the tuples to be retrieved. We might want that one or more attributes be equal to a certain value, or have a more relaxed query where some attributes lie within a certain range. A naive approach to solve such queries would be to scan all the tuples and check that all attributes are within the desired range. This would result in an algorithm of time complexity O(n*a), where n is the total number of tuples, and a is the number of attributes over which we are applying a constraint. However, we can consider the problem of solving a database query as a geometric one, where the constrained attributes constitute an n-dimensional space, and each tuple is a point in such space. Therefore, we can leverage theories of range searching to solve the query faster. In practice, databases build several indexes as data structures more efficient to be searched. This project seeks to study the complexity of different space-partitioning data structures used in computational geometry, and their applicability for solving range database queries efficiently.
