begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|Loggers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|nullValue
import|;
end_import

begin_class
DECL|class|CommitPointsTests
specifier|public
class|class
name|CommitPointsTests
extends|extends
name|ESTestCase
block|{
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|CommitPointsTests
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|testCommitPointXContent
specifier|public
name|void
name|testCommitPointXContent
parameter_list|()
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|CommitPoint
operator|.
name|FileInfo
argument_list|>
name|indexFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|indexFiles
operator|.
name|add
argument_list|(
operator|new
name|CommitPoint
operator|.
name|FileInfo
argument_list|(
literal|"file1"
argument_list|,
literal|"file1_p"
argument_list|,
literal|100
argument_list|,
literal|"ck1"
argument_list|)
argument_list|)
expr_stmt|;
name|indexFiles
operator|.
name|add
argument_list|(
operator|new
name|CommitPoint
operator|.
name|FileInfo
argument_list|(
literal|"file2"
argument_list|,
literal|"file2_p"
argument_list|,
literal|200
argument_list|,
literal|"ck2"
argument_list|)
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|CommitPoint
operator|.
name|FileInfo
argument_list|>
name|translogFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|translogFiles
operator|.
name|add
argument_list|(
operator|new
name|CommitPoint
operator|.
name|FileInfo
argument_list|(
literal|"t_file1"
argument_list|,
literal|"t_file1_p"
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|translogFiles
operator|.
name|add
argument_list|(
operator|new
name|CommitPoint
operator|.
name|FileInfo
argument_list|(
literal|"t_file2"
argument_list|,
literal|"t_file2_p"
argument_list|,
literal|200
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|CommitPoint
name|commitPoint
init|=
operator|new
name|CommitPoint
argument_list|(
literal|1
argument_list|,
literal|"test"
argument_list|,
name|CommitPoint
operator|.
name|Type
operator|.
name|GENERATED
argument_list|,
name|indexFiles
argument_list|,
name|translogFiles
argument_list|)
decl_stmt|;
name|byte
index|[]
name|serialized
init|=
name|CommitPoints
operator|.
name|toXContent
argument_list|(
name|commitPoint
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"serialized commit_point {}"
argument_list|,
operator|new
name|String
argument_list|(
name|serialized
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|CommitPoint
name|desCp
init|=
name|CommitPoints
operator|.
name|fromXContent
argument_list|(
name|serialized
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|version
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|version
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|indexFiles
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|indexFiles
argument_list|()
operator|.
name|size
argument_list|()
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
name|desCp
operator|.
name|indexFiles
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|desCp
operator|.
name|indexFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|indexFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|indexFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|physicalName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|indexFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|physicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|indexFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|length
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|indexFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|indexFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|checksum
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|indexFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|checksum
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|desCp
operator|.
name|translogFiles
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|translogFiles
argument_list|()
operator|.
name|size
argument_list|()
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
name|desCp
operator|.
name|indexFiles
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|desCp
operator|.
name|translogFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|translogFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|translogFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|physicalName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|translogFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|physicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|translogFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|length
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|commitPoint
operator|.
name|translogFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|desCp
operator|.
name|translogFiles
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|checksum
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

