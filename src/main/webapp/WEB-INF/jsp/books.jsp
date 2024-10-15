<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
  <title>Books</title>
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
<h1>Books List</h1>
<table>
  <tr>
    <th>Name</th>
    <th>Link</th>
  </tr>
  <c:forEach var="book" items="${requestScope.books}">
    <tr>
      <td>${book.title()}</td>
      <td>
        <a href="${Pagecontext.request.contextPath}books?id=${book.id()}">View</a> |
      </td>
    </tr>
  </c:forEach>
</table>
<br>
<table>
  <tr>
    <th>Other Library's objects</th>
    <th>Link</th>
  </tr>
  <tr>
    <td>All Readers</td>
    <td><a href="${PageContext.request.contextPath}/readers">View</a></td>
  </tr>
  <tr>
    <td>All Library</td>
    <td><a href="${PageContext.request.contextPath}/libraries">View</a></td>
  </tr>
</table>
</body>
</html>