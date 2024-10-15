<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>${requestScope.reader.readerName()}</title>
</head>
<body>
<h1>${requestScope.reader.readerName()}</h1>

<table>
    <tr>
        <th>Books</th>
        <th>Action</th>
    </tr>
</table>
<br>
<a href="http://localhost:8080/readers/">Back to Readers List</a>
</body>
</html>