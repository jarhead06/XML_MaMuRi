<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : testXSL.xsl
    Created on : 5 décembre 2013, 15:02
    Author     : Bastien
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <!-- TODO customize transformation rules
         syntax recommendation http://www.w3.org/TR/xslt
    -->
    <xsl:template match="/">
        <html>
            <head>
                <link href="../bootstrap/css/bootstrap.css" rel="stylesheet" media="screen"></link>
                <link href="../bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"></link>
            </head>
            <body>
                <table class="table table-hover">
                    <tr bgcolor="#F0F0F0">
                        <td>Nom hotel</td>
                        <td>ID</td>
                        <td>Phone</td>
                        <td>Label</td>
                    </tr>
                    <xsl:for-each select="entries/entry">
                        <tr>
                            <td>
                                <xsl:value-of select="name_fr"/>
                            </td>
                            <td>
                                <xsl:value-of select="ID"/>
                            </td>
                            <td>
                                <xsl:value-of select="phone"/>
                            </td>
                            <td>
                                <xsl:value-of select="labels/label"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
