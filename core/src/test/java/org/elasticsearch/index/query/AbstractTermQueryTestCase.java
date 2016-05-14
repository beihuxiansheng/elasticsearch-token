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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|io
operator|.
name|JsonStringEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|AbstractTermQueryTestCase
specifier|public
specifier|abstract
class|class
name|AbstractTermQueryTestCase
parameter_list|<
name|QB
extends|extends
name|BaseTermQueryBuilder
parameter_list|<
name|QB
parameter_list|>
parameter_list|>
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|QB
argument_list|>
block|{
DECL|method|createQueryBuilder
specifier|protected
specifier|abstract
name|QB
name|createQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
throws|throws
name|QueryShardException
block|{
try|try
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|createQueryBuilder
argument_list|(
literal|null
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createQueryBuilder
argument_list|(
literal|""
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"fieldname cannot be null or empty"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|createQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"value cannot be null or empty"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Override
DECL|method|getAlternateVersions
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|QB
argument_list|>
name|getAlternateVersions
parameter_list|()
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|QB
argument_list|>
name|alternateVersions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|QB
name|tempQuery
init|=
name|createTestQueryBuilder
argument_list|()
decl_stmt|;
name|QB
name|testQuery
init|=
name|createQueryBuilder
argument_list|(
name|tempQuery
operator|.
name|fieldName
argument_list|()
argument_list|,
name|tempQuery
operator|.
name|value
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|isString
init|=
name|testQuery
operator|.
name|value
argument_list|()
operator|instanceof
name|String
decl_stmt|;
name|Object
name|value
decl_stmt|;
if|if
condition|(
name|isString
condition|)
block|{
name|JsonStringEncoder
name|encoder
init|=
name|JsonStringEncoder
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|value
operator|=
literal|"\""
operator|+
operator|new
name|String
argument_list|(
name|encoder
operator|.
name|quoteAsString
argument_list|(
operator|(
name|String
operator|)
name|testQuery
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
operator|+
literal|"\""
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|testQuery
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
name|String
name|contentString
init|=
literal|"{\n"
operator|+
literal|"    \""
operator|+
name|testQuery
operator|.
name|getName
argument_list|()
operator|+
literal|"\" : {\n"
operator|+
literal|"        \""
operator|+
name|testQuery
operator|.
name|fieldName
argument_list|()
operator|+
literal|"\" : "
operator|+
name|value
operator|+
literal|"\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
decl_stmt|;
name|alternateVersions
operator|.
name|put
argument_list|(
name|contentString
argument_list|,
name|testQuery
argument_list|)
expr_stmt|;
return|return
name|alternateVersions
return|;
block|}
block|}
end_class

end_unit

