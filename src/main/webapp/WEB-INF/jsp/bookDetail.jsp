<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>${requestScope.book.title()}</title>
    <style>
        table {
            border-collapse: collapse; /* Убирает двойные границы */
            width: 100%; /* Ширина таблицы */
        }
        th, td {
            border: 2px solid black; /* Толщина и цвет линий */
            padding: 10px; /* Отступы внутри ячеек */
            text-align: left; /* Выравнивание текста */
        }
        th {
            background-color: #f2f2f2; /* Цвет фона заголовков */
        }
    </style>
</head>
<body>
<h1>${requestScope.book.title()}</h1>

<table>
    <tr>
        <th>Author</th>
        <th> Belongs to the library </th>
        <th> Readers read: </th>
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
            <c:choose>
                <c:when test="${not empty requestScope.readers}">
                    <c:forEach var="reader" items="${requestScope.readers}">
                        <a href="${PageContext.request.contextPath}/readers?id=${reader.id()}">${reader.readerName()}</a>
                        <br>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    Readers don't read this book at the moment.
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</table>
<br>
<a href="http://localhost:8080/books">Back to Books List</a>
</body>
</html>