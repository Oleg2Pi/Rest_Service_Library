<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>${requestScope.reader.readerName()}</title>
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
<h1>${requestScope.reader.readerName()}</h1>

<table>
    <c:choose>
        <c:when test="${not empty requestScope.books}">
            <tr>
                <th> Books title </th>
                <th> Link </th>
                <th> Action </th>
            </tr>
            <c:forEach var="book" items="${requestScope.books}">
                <tr>
                    <td>${book.title()}</td>
                    <td><a href="${PageContect.request.contextPath}/books?id=${book.id()}">View</a></td>
                    <td>
                        <form action="readers" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${requestScope.reader.id()}"/>
                            <input type="hidden" name="bookId" value="${book.id()}"/>
                            <input type="submit" value="Delete"/>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            Not found books
        </c:otherwise>
    </c:choose>
</table>
<br>
<h1>Add Book </h1>
<c:choose>
    <c:when test="${not empty requestScope.booksNot}">
        <form action="readers" method="post">
            <select name="bookIdSave">
                <c:forEach var="book" items="${requestScope.booksNot}">
                    <option value="${book.id()}">${book.title()}</option>
                </c:forEach>
            </select>
            <input type="hidden" name="id" value="${requestScope.reader.id()}"/>
            <input type="submit" value="Add"/>
        </form>
    </c:when>
    <c:otherwise>
        You already added all books.
    </c:otherwise>
</c:choose>
<br>
<a href="http://localhost:8080/readers">Back to Readers List</a>
</body>
</html>