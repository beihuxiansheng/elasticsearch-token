begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|FieldTypeTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|AnalyzingCompletionLookupProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|context
operator|.
name|ContextBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|context
operator|.
name|ContextMapping
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_class
DECL|class|CompletionFieldTypeTests
specifier|public
class|class
name|CompletionFieldTypeTests
extends|extends
name|FieldTypeTestCase
block|{
annotation|@
name|Override
DECL|method|createDefaultFieldType
specifier|protected
name|MappedFieldType
name|createDefaultFieldType
parameter_list|()
block|{
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|ft
init|=
operator|new
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
argument_list|()
decl_stmt|;
name|ft
operator|.
name|setProvider
argument_list|(
operator|new
name|AnalyzingCompletionLookupProvider
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ft
return|;
block|}
annotation|@
name|Before
DECL|method|setupProperties
specifier|public
name|void
name|setupProperties
parameter_list|()
block|{
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"preserve_separators"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|cft
init|=
operator|(
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
operator|)
name|ft
decl_stmt|;
name|cft
operator|.
name|setProvider
argument_list|(
operator|new
name|AnalyzingCompletionLookupProvider
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"preserve_position_increments"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|cft
init|=
operator|(
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
operator|)
name|ft
decl_stmt|;
name|cft
operator|.
name|setProvider
argument_list|(
operator|new
name|AnalyzingCompletionLookupProvider
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"payload"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|cft
init|=
operator|(
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
operator|)
name|ft
decl_stmt|;
name|cft
operator|.
name|setProvider
argument_list|(
operator|new
name|AnalyzingCompletionLookupProvider
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addModifier
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"context_mapping"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|cft
init|=
operator|(
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
operator|)
name|ft
decl_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|ContextMapping
argument_list|>
name|contextMapping
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|contextMapping
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|ContextBuilder
operator|.
name|location
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|cft
operator|.
name|setContextMapping
argument_list|(
name|contextMapping
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
