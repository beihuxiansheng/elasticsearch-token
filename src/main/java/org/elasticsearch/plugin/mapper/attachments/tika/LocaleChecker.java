begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.mapper.attachments.tika
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|mapper
operator|.
name|attachments
operator|.
name|tika
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
name|Constants
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Integer
operator|.
name|parseInt
import|;
end_import

begin_class
DECL|class|LocaleChecker
specifier|public
class|class
name|LocaleChecker
block|{
DECL|field|JVM_MAJOR_VERSION
specifier|public
specifier|static
name|int
name|JVM_MAJOR_VERSION
init|=
literal|0
decl_stmt|;
DECL|field|JVM_MINOR_VERSION
specifier|public
specifier|static
name|int
name|JVM_MINOR_VERSION
init|=
literal|0
decl_stmt|;
DECL|field|JVM_PATCH_MAJOR_VERSION
specifier|public
specifier|static
name|int
name|JVM_PATCH_MAJOR_VERSION
init|=
literal|0
decl_stmt|;
DECL|field|JVM_PATCH_MINOR_VERSION
specifier|public
specifier|static
name|int
name|JVM_PATCH_MINOR_VERSION
init|=
literal|0
decl_stmt|;
static|static
block|{
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|Constants
operator|.
name|JAVA_VERSION
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
name|JVM_MAJOR_VERSION
operator|=
name|parseInt
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|JVM_MINOR_VERSION
operator|=
name|parseInt
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|StringTokenizer
name|stPatch
init|=
operator|new
name|StringTokenizer
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
name|JVM_PATCH_MAJOR_VERSION
operator|=
name|parseInt
argument_list|(
name|stPatch
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|JVM_PATCH_MINOR_VERSION
operator|=
name|parseInt
argument_list|(
name|stPatch
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * We can have issues with some JVMs and Locale      * See https://github.com/elasticsearch/elasticsearch-mapper-attachments/issues/105      */
DECL|method|isLocaleCompatible
specifier|public
specifier|static
name|boolean
name|isLocaleCompatible
parameter_list|()
block|{
name|String
name|language
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
decl_stmt|;
name|boolean
name|acceptedLocale
init|=
literal|true
decl_stmt|;
if|if
condition|(
comment|// We can have issues with JDK7 Patch< 80
operator|(
name|JVM_MAJOR_VERSION
operator|==
literal|1
operator|&&
name|JVM_MINOR_VERSION
operator|==
literal|7
operator|&&
name|JVM_PATCH_MAJOR_VERSION
operator|==
literal|0
operator|&&
name|JVM_PATCH_MINOR_VERSION
operator|<
literal|80
operator|)
operator|||
comment|// We can have issues with JDK8 Patch< 40
operator|(
name|JVM_MAJOR_VERSION
operator|==
literal|1
operator|&&
name|JVM_MINOR_VERSION
operator|==
literal|8
operator|&&
name|JVM_PATCH_MAJOR_VERSION
operator|==
literal|0
operator|&&
name|JVM_PATCH_MINOR_VERSION
operator|<
literal|40
operator|)
condition|)
block|{
if|if
condition|(
name|language
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"tr"
argument_list|)
operator|||
name|language
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"az"
argument_list|)
condition|)
block|{
name|acceptedLocale
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|acceptedLocale
return|;
block|}
block|}
end_class

end_unit

