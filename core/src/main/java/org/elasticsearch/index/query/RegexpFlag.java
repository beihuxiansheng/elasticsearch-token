begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|RegExp
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Regular expression syntax flags. Each flag represents optional syntax support in the regular expression:  *<ul>  *<li><tt>INTERSECTION</tt> - Support for intersection notation:<tt>&lt;expression&gt;&amp;&lt;expression&gt;</tt></li>  *<li><tt>COMPLEMENT</tt> - Support for complement notation:<tt>&lt;expression&gt;&amp;&lt;expression&gt;</tt></li>  *<li><tt>EMPTY</tt> - Support for the empty language symbol:<tt>#</tt></li>  *<li><tt>ANYSTRING</tt> - Support for the any string symbol:<tt>@</tt></li>  *<li><tt>INTERVAL</tt> - Support for numerical interval notation:<tt>&lt;n-m&gt;</tt></li>  *<li><tt>NONE</tt> - Disable support for all syntax options</li>  *<li><tt>ALL</tt> - Enables support for all syntax options</li>  *</ul>  *  * @see RegexpQueryBuilder#flags(RegexpFlag...)  * @see RegexpQueryBuilder#flags(RegexpFlag...)  */
end_comment

begin_enum
DECL|enum|RegexpFlag
specifier|public
enum|enum
name|RegexpFlag
block|{
comment|/**      * Enables intersection of the form:<tt>&lt;expression&gt;&amp;&lt;expression&gt;</tt>      */
DECL|enum constant|INTERSECTION
name|INTERSECTION
parameter_list|(
name|RegExp
operator|.
name|INTERSECTION
parameter_list|)
operator|,
comment|/**      * Enables complement expression of the form:<tt>~&lt;expression&gt;</tt>      */
DECL|enum constant|COMPLEMENT
constructor|COMPLEMENT(RegExp.COMPLEMENT
block|)
enum|,
comment|/**      * Enables empty language expression:<tt>#</tt>      */
DECL|enum constant|EMPTY
name|EMPTY
argument_list|(
name|RegExp
operator|.
name|EMPTY
argument_list|)
operator|,
comment|/**      * Enables any string expression:<tt>@</tt>      */
DECL|enum constant|ANYSTRING
name|ANYSTRING
argument_list|(
name|RegExp
operator|.
name|ANYSTRING
argument_list|)
operator|,
comment|/**      * Enables numerical interval expression:<tt>&lt;n-m&gt;</tt>      */
DECL|enum constant|INTERVAL
name|INTERVAL
argument_list|(
name|RegExp
operator|.
name|INTERVAL
argument_list|)
operator|,
comment|/**      * Disables all available option flags      */
DECL|enum constant|NONE
name|NONE
argument_list|(
name|RegExp
operator|.
name|NONE
argument_list|)
operator|,
comment|/**      * Enables all available option flags      */
DECL|enum constant|ALL
name|ALL
argument_list|(
name|RegExp
operator|.
name|ALL
argument_list|)
enum|;
end_enum

begin_decl_stmt
DECL|field|value
specifier|final
name|int
name|value
decl_stmt|;
end_decl_stmt

begin_constructor
DECL|method|RegexpFlag
specifier|private
name|RegexpFlag
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
end_constructor

begin_function
DECL|method|value
specifier|public
name|int
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
end_function

begin_comment
comment|/**      * Resolves the combined OR'ed value for the given list of regular expression flags. The given flags must follow the      * following syntax:      *<p>      *<tt>flag_name</tt>(|<tt>flag_name</tt>)*      *<p>      * Where<tt>flag_name</tt> is one of the following:      *<ul>      *<li>INTERSECTION</li>      *<li>COMPLEMENT</li>      *<li>EMPTY</li>      *<li>ANYSTRING</li>      *<li>INTERVAL</li>      *<li>NONE</li>      *<li>ALL</li>      *</ul>      *<p>      * Example:<tt>INTERSECTION|COMPLEMENT|EMPTY</tt>      *      * @param flags A string representing a list of regular expression flags      * @return The combined OR'ed value for all the flags      */
end_comment

begin_function
DECL|method|resolveValue
specifier|public
specifier|static
name|int
name|resolveValue
parameter_list|(
name|String
name|flags
parameter_list|)
block|{
if|if
condition|(
name|flags
operator|==
literal|null
operator|||
name|flags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|RegExp
operator|.
name|ALL
return|;
block|}
name|int
name|magic
init|=
name|RegExp
operator|.
name|NONE
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|Strings
operator|.
name|delimitedListToStringArray
argument_list|(
name|flags
argument_list|,
literal|"|"
argument_list|)
control|)
block|{
if|if
condition|(
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|RegexpFlag
name|flag
init|=
name|RegexpFlag
operator|.
name|valueOf
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|flag
operator|==
name|RegexpFlag
operator|.
name|NONE
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|flag
operator|==
name|RegexpFlag
operator|.
name|ALL
condition|)
block|{
return|return
name|flag
operator|.
name|value
argument_list|()
return|;
block|}
name|magic
operator||=
name|flag
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown regexp flag ["
operator|+
name|s
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
return|return
name|magic
return|;
block|}
end_function

unit|}
end_unit

