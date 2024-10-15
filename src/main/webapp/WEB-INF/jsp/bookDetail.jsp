<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>${requestScope.book.title()}</title>
</head>
<body>
<h1>${requestScope.book.title()}</h1>

<table>
    <tr>
        <th>Author</th>
        <th> Belongs to the library </th>
        <th> Given to the reader </th>
    </tr>
    <tr>
        <td>
            ${requestScope.book.author()}
        </td>
        <td>
            <a href="${PageContext.request.contextPath}/libraries?id=${requestScope.book.library().getId()}">
                ${requestScope.book.library().getLibraryName()}
            </a>
        </td>
        <td>

        </td>
    </tr>
</table>
<br>
<a href="http://localhost:8080/books">Back to Books List</a>
</body>
</html>