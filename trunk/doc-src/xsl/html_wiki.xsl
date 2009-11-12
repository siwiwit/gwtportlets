<?xml version="1.0" ?>
<!--
  ~ GWT Portlets Framework (http://code.google.com/p/gwtportlets/)
  ~ Copyright 2009 Business Systems Group (Africa)
  ~
  ~ This file is part of GWT Portlets.
  ~
  ~ GWT Portlets is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Lesser General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ GWT Portlets is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public License
  ~ along with GWT Portlets.  If not, see <http://www.gnu.org/licenses/>.
  -->

<!--
    This stylesheet is based on the one used to generate the Spring
    documentation. It produces chunked HTML for conversion into Google
    Code Wiki markup.
-->
<!-- 
    This is the XSL HTML configuration file for the Spring
    Reference Documentation.
-->
<!DOCTYPE xsl:stylesheet [
    <!ENTITY db_xsl_path        "docbook">
    <!ENTITY callout_gfx_path   "../images/callouts/">
    <!ENTITY admon_gfx_path     "../images/admons/">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/TR/xhtml1/transitional"
	exclude-result-prefixes="#default">
	<xsl:import href="&db_xsl_path;/html/chunk.xsl" />
	<!--###################################################
                     HTML Settings
    ################################################### -->
	<xsl:param name="chunk.section.depth">'5'</xsl:param>
	<xsl:param name="use.id.as.filename">'1'</xsl:param>
    <xsl:param name="html.stylesheet">html.css</xsl:param>
	<!-- These extensions are required for table printing and other stuff -->
	<xsl:param name="use.extensions">1</xsl:param>
	<xsl:param name="tablecolumns.extension">0</xsl:param>
	<xsl:param name="callout.extensions">1</xsl:param>
	<xsl:param name="graphicsize.extension">0</xsl:param>
    <xsl:param name="ignore.image.scaling">1</xsl:param>
	<!--###################################################
                      Table Of Contents
    ################################################### -->
	<!-- Generate the TOCs for named components only -->
	<xsl:param name="generate.toc">
        book   toc
    </xsl:param>
	<!-- Show only Sections up to level 3 in the TOCs -->
	<xsl:param name="toc.section.depth">3</xsl:param>
	<!--###################################################
                         Labels
    ################################################### -->
	<!-- Label Chapters and Sections (numbering) -->
	<xsl:param name="chapter.autolabel">1</xsl:param>
	<xsl:param name="section.autolabel" select="1" />
	<xsl:param name="section.label.includes.component.label" select="1" />
	<!--###################################################
                         Callouts
    ################################################### -->
	<!-- Use images for callouts instead of (1) (2) (3) -->
	<xsl:param name="callout.graphics">1</xsl:param>
	<xsl:param name="callout.graphics.path">&callout_gfx_path;</xsl:param>
	<!-- Place callout marks at this column in annotated areas -->
	<xsl:param name="callout.defaultcolumn">90</xsl:param>
	<!--###################################################
                       Admonitions
    ################################################### -->
	<!-- Use nice graphics for admonitions -->
	<xsl:param name="admon.graphics">'1'</xsl:param>
	<xsl:param name="admon.graphics.path">&admon_gfx_path;</xsl:param>
	<!--###################################################
                          Misc
    ################################################### -->
	<!-- Placement of titles -->
	<xsl:param name="formal.title.placement">
        figure after
        example before
        equation before
        table before
        procedure before
    </xsl:param>
	<xsl:template match="author" mode="titlepage.mode">
		<xsl:if test="name(preceding-sibling::*[1]) = 'author'">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<span class="{name(.)}">
			<xsl:call-template name="person.name" />
			<xsl:apply-templates mode="titlepage.mode" select="./contrib" />
			<xsl:apply-templates mode="titlepage.mode" select="./affiliation" />
		</span>
	</xsl:template>
	<xsl:template match="authorgroup" mode="titlepage.mode">
		<div class="{name(.)}">
			<xsl:apply-templates mode="titlepage.mode" />
		</div>
	</xsl:template>
<!--###################################################
                     Headers and Footers
    ################################################### --> 
    
	<!-- use title as Google Code Wiki page summary line -->
    <xsl:template name="header.navigation">
        <xsl:text>#summary Manual </xsl:text><xsl:apply-templates select="." mode="object.title.markup"/>
        <p><xsl:text>&gt;&gt; [Introduction] / [Portlets] / [Concepts] / [AppStructure Application Structure] / [Layouts] / [PageEditor Page Editor] / [Widgets Widgets] / [Themes] / [Spring] &lt;&lt;</xsl:text></p>
    </xsl:template>

    <!-- no footer navigation -->
    <xsl:template name="footer.navigation">
        <p><xsl:text>&gt;&gt; [Introduction] / [Portlets] / [Concepts] / [AppStructure Application Structure] / [Layouts] / [PageEditor Page Editor] / [Widgets Widgets] / [Themes] / [Spring] &lt;&lt;</xsl:text></p>
    </xsl:template>

    <!--<xsl:param name="navig.showtitles">1</xsl:param>-->

    <!-- let's have a Powered by Interface21 banner across the bottom of each page -->
    <!--<xsl:template name="user.footer.navigation">-->
		<!--<div style="background-color:#31430f;border:none;height:50px;border:1px solid black;text-align:center">-->
			<!--<table cellspacing="0" cellpadding="0" width="100%" style="margin: 0px; padding: 0px">-->
        		<!--<tr>-->
        			<!--<td width="50%" style="font-size: 110%; color: white; vertical-align: center; text-align: right">-->
        				<!--Sponsored by-->
        			<!--</td>-->
        			<!--<td width="50%" style="padding: 0px">-->
        				<!--<a style="border:none;background: url();" href="http://www.interface21.com/" title="Brought to you by Interface21"><img style="border: none" src="images/interface21.gif"/></a>-->
        			<!--</td>-->
        		<!--</tr>-->
        	<!--</table>-->
		<!--</div>-->
     <!--</xsl:template>-->

</xsl:stylesheet>
