begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.common.lucene
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|common
operator|.
name|lucene
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsEqual
operator|.
name|equalTo
import|;
end_import

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
name|Version
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|ESLoggerFactory
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
name|lucene
operator|.
name|Lucene
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|LuceneTest
specifier|public
class|class
name|LuceneTest
block|{
comment|/*      * simple test that ensures that we bumb the version on Upgrade      */
annotation|@
name|Test
DECL|method|testVersion
specifier|public
name|void
name|testVersion
parameter_list|()
block|{
name|ESLogger
name|logger
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Version
index|[]
name|values
init|=
name|Version
operator|.
name|values
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|equalTo
argument_list|(
name|values
index|[
name|values
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"Latest Lucene Version is not set after upgrade"
argument_list|,
name|Lucene
operator|.
name|VERSION
argument_list|,
name|equalTo
argument_list|(
name|values
index|[
name|values
operator|.
name|length
operator|-
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Lucene
operator|.
name|parseVersion
argument_list|(
literal|null
argument_list|,
name|Lucene
operator|.
name|VERSION
argument_list|,
literal|null
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
comment|// this should fail if the lucene version is not mapped as a string in Lucene.java
name|assertThat
argument_list|(
name|Lucene
operator|.
name|parseVersion
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|name
argument_list|()
operator|.
name|replaceFirst
argument_list|(
literal|"^LUCENE_(\\d)(\\d)$"
argument_list|,
literal|"$1.$2"
argument_list|)
argument_list|,
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|logger
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

