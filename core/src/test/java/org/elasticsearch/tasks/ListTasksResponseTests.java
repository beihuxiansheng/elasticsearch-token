begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|tasks
operator|.
name|list
operator|.
name|ListTasksResponse
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_class
DECL|class|ListTasksResponseTests
specifier|public
class|class
name|ListTasksResponseTests
extends|extends
name|ESTestCase
block|{
DECL|method|testEmptyToString
specifier|public
name|void
name|testEmptyToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"{\"tasks\":{}}"
argument_list|,
operator|new
name|ListTasksResponse
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonEmptyToString
specifier|public
name|void
name|testNonEmptyToString
parameter_list|()
block|{
name|TaskInfo
name|info
init|=
operator|new
name|TaskInfo
argument_list|(
operator|new
name|TaskId
argument_list|(
literal|"node1"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"dummy-type"
argument_list|,
literal|"dummy-action"
argument_list|,
literal|"dummy-description"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
operator|new
name|TaskId
argument_list|(
literal|"node1"
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|ListTasksResponse
name|tasksResponse
init|=
operator|new
name|ListTasksResponse
argument_list|(
name|singletonList
argument_list|(
name|info
argument_list|)
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"tasks\":{\"node1:1\":{\"node\":\"node1\",\"id\":1,\"type\":\"dummy-type\",\"action\":\"dummy-action\","
operator|+
literal|"\"description\":\"dummy-description\",\"start_time_in_millis\":0,\"running_time_in_nanos\":1,\"cancellable\":true,"
operator|+
literal|"\"parent_task_id\":\"node1:0\"}}}"
argument_list|,
name|tasksResponse
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

