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
    <th>Actions</th>
  </tr>
  <c:forEach var="book" items="${requestScope.books}">
    <tr>
      <td>${book.title()}</td>
      <td>
        <a href="${Pagecontext.request.contextPath}books?id=${book.id()}">View</a> |
        <form action="books" method="post" style="display:inline;">
          <input type="hidden" name="id" value="${book.id()}"/>
          <!-- Optionally add hidden input for update -->
          <input type="text" name="title" required placeholder="Title"/>
          <input type="text" name="author" required placeholder="Author"/>
          <input type="text" name="libraryId" required placeholder="Library ID"/>
          <input type="submit" value="Update"/>
        </form> |
        <form action="books" method="post" style="display:inline;">
          <input type="hidden" name="id" value="${book.id()}"/>
          <input type="submit" value="Delete"/>
        </form>
      </td>
    </tr>
  </c:forEach>
</table>

<h2>Add New Book</h2>
<form action="books" method="post">
  <input type="text" name="title" required placeholder="Title"/>
  <input type="text" name="author" required placeholder="Author"/>
  <input type="text" name="libraryId" required placeholder="Library ID"/>
  <input type="submit" value="Add Book"/>
</form>

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
    <td><a href="${PageContext.request.contextPath}/">View</a></td>
  </tr>
</table>
</body>
</html>