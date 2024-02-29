<!DOCTYPE html>
<html lang="ts">
<head>
    <title>官网</title>
</head>
<body>
<h1>欢迎来到我的网站</h1>
<ul>
    <#-- 循环渲染导航条 -->
    <#list menuItems as item>
        <li>
            <a href="${item.url}">${item.label}</a>
        </li>
    </#list>
</ul>
<#-- 底部版权信息 -->
<footer>
    ${currentYear?c} All rights reserved
</footer>
</body>
</html>