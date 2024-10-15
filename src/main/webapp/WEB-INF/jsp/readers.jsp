<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>Readers</title>
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
<h1>Readers List</h1>

<table>
    <tr>
        <th>Name</th>
        <th>Actions</th>
    </tr>
    <c:forEach var="reader" items="${requestScope.readers}">
        <tr>
            <td>${reader.readerName()}</td>
            <td>
                <a href="${Pagecontext.request.contextPath}readers?id=${reader.id()}">View</a> |
                <form action="readers" method="post" style="display:inline;">
                    <input type="hidden" name="id" value="${reader.id()}"/>
                    <!-- Optionally add hidden input for update -->
                    <input type="text" name="readerName" required placeholder="Reader Name"/>
                    <input type="submit" value="Update"/>
                </form> |
                <form action="readers" method="post" style="display:inline;">
                    <input type="hidden" name="id" value="${reader.id()}"/>
                    <input type="submit" value="Delete"/>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>

<h2>Create a New Reader</h2>
<form action="readers" method="post">
    <input type="text" name="readerName" required placeholder="Reader Name"/>
    <input type="submit" value="Add Reader"/>
</form>

<table>
    <tr>
        <th>Other Library's objects</th>
        <th>Link</th>
    </tr>
    <tr>
        <td>All Libraries</td>
        <td><a href="${PageContext.request.contextPath}/libraries">View</a></td>
    </tr>
    <tr>
        <td>All Books</td>
        <td><a href="${PageContext.request.contextPath}/books">View</a></td>
    </tr>
</table>
</body>
</html>