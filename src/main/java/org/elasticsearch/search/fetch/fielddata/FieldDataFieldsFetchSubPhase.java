begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|fielddata
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|inject
operator|.
name|Inject
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
name|fielddata
operator|.
name|AtomicFieldData
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
name|fielddata
operator|.
name|ScriptDocValues
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
name|FieldMapper
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
name|SearchHitField
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
name|SearchParseElement
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
name|fetch
operator|.
name|FetchSubPhase
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
name|internal
operator|.
name|InternalSearchHit
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
name|internal
operator|.
name|InternalSearchHitField
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
name|internal
operator|.
name|SearchContext
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

begin_comment
comment|/**  * Query sub phase which pulls data from field data (using the cache if  * available, building it if not).  *  * Specifying {@code "fielddata_fields": ["field1", "field2"]}  */
end_comment

begin_class
DECL|class|FieldDataFieldsFetchSubPhase
specifier|public
class|class
name|FieldDataFieldsFetchSubPhase
implements|implements
name|FetchSubPhase
block|{
annotation|@
name|Inject
DECL|method|FieldDataFieldsFetchSubPhase
specifier|public
name|FieldDataFieldsFetchSubPhase
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|parseElements
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchParseElement
argument_list|>
name|parseElements
parameter_list|()
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|SearchParseElement
argument_list|>
name|parseElements
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|parseElements
operator|.
name|put
argument_list|(
literal|"fielddata_fields"
argument_list|,
operator|new
name|FieldDataFieldsParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"fielddataFields"
argument_list|,
operator|new
name|FieldDataFieldsParseElement
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parseElements
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hitsExecutionNeeded
specifier|public
name|boolean
name|hitsExecutionNeeded
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hitsExecute
specifier|public
name|void
name|hitsExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|InternalSearchHit
index|[]
name|hits
parameter_list|)
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|hitExecutionNeeded
specifier|public
name|boolean
name|hitExecutionNeeded
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|hasFieldDataFields
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hitExecute
specifier|public
name|void
name|hitExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|HitContext
name|hitContext
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
for|for
control|(
name|FieldDataFieldsContext
operator|.
name|FieldDataField
name|field
range|:
name|context
operator|.
name|fieldDataFields
argument_list|()
operator|.
name|fields
argument_list|()
control|)
block|{
if|if
condition|(
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|fieldsOrNull
argument_list|()
operator|==
literal|null
condition|)
block|{
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|fields
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SearchHitField
argument_list|>
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SearchHitField
name|hitField
init|=
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|hitField
operator|==
literal|null
condition|)
block|{
name|hitField
operator|=
operator|new
name|InternalSearchHitField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|fields
argument_list|()
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|hitField
argument_list|)
expr_stmt|;
block|}
name|FieldMapper
name|mapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|AtomicFieldData
name|data
init|=
name|context
operator|.
name|fieldData
argument_list|()
operator|.
name|getForField
argument_list|(
name|mapper
argument_list|)
operator|.
name|load
argument_list|(
name|hitContext
operator|.
name|readerContext
argument_list|()
argument_list|)
decl_stmt|;
name|ScriptDocValues
name|values
init|=
name|data
operator|.
name|getScriptValues
argument_list|()
decl_stmt|;
name|values
operator|.
name|setNextDocId
argument_list|(
name|hitContext
operator|.
name|docId
argument_list|()
argument_list|)
expr_stmt|;
name|hitField
operator|.
name|values
argument_list|()
operator|.
name|addAll
argument_list|(
name|values
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

