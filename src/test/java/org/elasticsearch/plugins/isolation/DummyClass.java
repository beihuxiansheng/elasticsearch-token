begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins.isolation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|isolation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
DECL|class|DummyClass
specifier|public
class|class
name|DummyClass
block|{
DECL|field|name
specifier|static
specifier|final
name|String
name|name
decl_stmt|;
static|static
block|{
name|Properties
name|sysProps
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
comment|// make sure to get a string even when dealing with null
name|name
operator|=
literal|""
operator|+
name|sysProps
operator|.
name|getProperty
argument_list|(
literal|"es.test.isolated.plugin.name"
argument_list|)
expr_stmt|;
name|sysProps
operator|.
name|setProperty
argument_list|(
literal|"es.test.isolated.plugin.instantiated"
argument_list|,
literal|""
operator|+
name|DummyClass
operator|.
name|class
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Integer
name|count
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"es.test.isolated.plugin.count"
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
name|count
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|count
operator|=
name|count
operator|+
literal|1
expr_stmt|;
name|sysProps
operator|.
name|setProperty
argument_list|(
literal|"es.test.isolated.plugin.count"
argument_list|,
name|count
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|prop
init|=
name|sysProps
operator|.
name|getProperty
argument_list|(
literal|"es.test.isolated.plugin.instantiated.hashes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|prop
operator|==
literal|null
condition|)
block|{
name|prop
operator|=
literal|""
expr_stmt|;
block|}
name|prop
operator|=
name|prop
operator|+
name|DummyClass
operator|.
name|class
operator|.
name|hashCode
argument_list|()
operator|+
literal|" "
expr_stmt|;
name|sysProps
operator|.
name|setProperty
argument_list|(
literal|"es.test.isolated.plugin.instantiated.hashes"
argument_list|,
name|prop
argument_list|)
expr_stmt|;
name|sysProps
operator|.
name|setProperty
argument_list|(
literal|"es.test.isolated.plugin.read.name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

