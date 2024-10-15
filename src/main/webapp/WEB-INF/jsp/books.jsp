<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
  <title>Books</title>
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