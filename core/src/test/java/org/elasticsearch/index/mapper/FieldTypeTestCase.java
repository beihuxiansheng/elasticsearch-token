begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|settings
operator|.
name|Settings
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|FieldDataType
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
name|similarity
operator|.
name|BM25SimilarityProvider
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Base test case for subclasses of MappedFieldType */
end_comment

begin_class
DECL|class|FieldTypeTestCase
specifier|public
specifier|abstract
class|class
name|FieldTypeTestCase
extends|extends
name|ESTestCase
block|{
comment|/** Abstraction for mutating a property of a MappedFieldType */
DECL|class|Modifier
specifier|public
specifier|static
specifier|abstract
class|class
name|Modifier
block|{
comment|/** The name of the property that is being modified. Used in test failure messages. */
DECL|field|property
specifier|public
specifier|final
name|String
name|property
decl_stmt|;
comment|/** true if this modifier only makes types incompatible in strict mode, false otherwise */
DECL|field|strictOnly
specifier|public
specifier|final
name|boolean
name|strictOnly
decl_stmt|;
comment|/** true if reversing the order of checkCompatibility arguments should result in the same conflicts, false otherwise **/
DECL|field|symmetric
specifier|public
specifier|final
name|boolean
name|symmetric
decl_stmt|;
DECL|method|Modifier
specifier|public
name|Modifier
parameter_list|(
name|String
name|property
parameter_list|,
name|boolean
name|strictOnly
parameter_list|,
name|boolean
name|symmetric
parameter_list|)
block|{
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
name|this
operator|.
name|strictOnly
operator|=
name|strictOnly
expr_stmt|;
name|this
operator|.
name|symmetric
operator|=
name|symmetric
expr_stmt|;
block|}
comment|/** Modifies the property */
DECL|method|modify
specifier|public
specifier|abstract
name|void
name|modify
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
function_decl|;
comment|/**          * Optional method to implement that allows the field type that will be compared to be modified,          * so that it does not have the default value for the property being modified.          */
DECL|method|normalizeOther
specifier|public
name|void
name|normalizeOther
parameter_list|(
name|MappedFieldType
name|other
parameter_list|)
block|{}
block|}
DECL|field|modifiers
specifier|private
specifier|final
name|List
argument_list|<
name|Modifier
argument_list|>
name|modifiers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Modifier
argument_list|(
literal|"boost"
argument_list|,
literal|true
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
name|ft
operator|.
name|setBoost
argument_list|(
literal|1.1f
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"doc_values"
argument_list|,
literal|false
argument_list|,
literal|false
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
name|ft
operator|.
name|setHasDocValues
argument_list|(
name|ft
operator|.
name|hasDocValues
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"analyzer"
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
name|ft
operator|.
name|setIndexAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"bar"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"analyzer"
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
name|ft
operator|.
name|setIndexAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"bar"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalizeOther
parameter_list|(
name|MappedFieldType
name|other
parameter_list|)
block|{
name|other
operator|.
name|setIndexAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"search_analyzer"
argument_list|,
literal|true
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
name|ft
operator|.
name|setSearchAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"bar"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"search_analyzer"
argument_list|,
literal|true
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
name|ft
operator|.
name|setSearchAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"bar"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalizeOther
parameter_list|(
name|MappedFieldType
name|other
parameter_list|)
block|{
name|other
operator|.
name|setSearchAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"search_quote_analyzer"
argument_list|,
literal|true
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
name|ft
operator|.
name|setSearchQuoteAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"bar"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"search_quote_analyzer"
argument_list|,
literal|true
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
name|ft
operator|.
name|setSearchQuoteAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"bar"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalizeOther
parameter_list|(
name|MappedFieldType
name|other
parameter_list|)
block|{
name|other
operator|.
name|setSearchQuoteAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"similarity"
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
name|ft
operator|.
name|setSimilarity
argument_list|(
operator|new
name|BM25SimilarityProvider
argument_list|(
literal|"foo"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"similarity"
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
name|ft
operator|.
name|setSimilarity
argument_list|(
operator|new
name|BM25SimilarityProvider
argument_list|(
literal|"foo"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalizeOther
parameter_list|(
name|MappedFieldType
name|other
parameter_list|)
block|{
name|other
operator|.
name|setSimilarity
argument_list|(
operator|new
name|BM25SimilarityProvider
argument_list|(
literal|"bar"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"norms.loading"
argument_list|,
literal|true
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
name|ft
operator|.
name|setNormsLoading
argument_list|(
name|MappedFieldType
operator|.
name|Loading
operator|.
name|LAZY
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"fielddata"
argument_list|,
literal|true
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
name|ft
operator|.
name|setFieldDataType
argument_list|(
operator|new
name|FieldDataType
argument_list|(
literal|"foo"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"loading"
argument_list|,
literal|"eager"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
operator|new
name|Modifier
argument_list|(
literal|"null_value"
argument_list|,
literal|true
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
name|ft
operator|.
name|setNullValue
argument_list|(
name|dummyNullValue
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Add a mutation that will be tested for all expected semantics of equality and compatibility.      * These should be added in an @Before method.      */
DECL|method|addModifier
specifier|protected
name|void
name|addModifier
parameter_list|(
name|Modifier
name|modifier
parameter_list|)
block|{
name|modifiers
operator|.
name|add
argument_list|(
name|modifier
argument_list|)
expr_stmt|;
block|}
DECL|field|dummyNullValue
specifier|private
name|Object
name|dummyNullValue
init|=
literal|"dummyvalue"
decl_stmt|;
comment|/** Sets the null value used by the modifier for null value testing. This should be set in an @Before method. */
DECL|method|setDummyNullValue
specifier|protected
name|void
name|setDummyNullValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|dummyNullValue
operator|=
name|value
expr_stmt|;
block|}
comment|/** Create a default constructed fieldtype */
DECL|method|createDefaultFieldType
specifier|protected
specifier|abstract
name|MappedFieldType
name|createDefaultFieldType
parameter_list|()
function_decl|;
DECL|method|createNamedDefaultFieldType
name|MappedFieldType
name|createNamedDefaultFieldType
parameter_list|()
block|{
name|MappedFieldType
name|fieldType
init|=
name|createDefaultFieldType
argument_list|()
decl_stmt|;
name|fieldType
operator|.
name|setNames
argument_list|(
operator|new
name|MappedFieldType
operator|.
name|Names
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fieldType
return|;
block|}
comment|// TODO: remove this once toString is no longer final on FieldType...
DECL|method|assertFieldTypeEquals
specifier|protected
name|void
name|assertFieldTypeEquals
parameter_list|(
name|String
name|property
parameter_list|,
name|MappedFieldType
name|ft1
parameter_list|,
name|MappedFieldType
name|ft2
parameter_list|)
block|{
if|if
condition|(
name|ft1
operator|.
name|equals
argument_list|(
name|ft2
argument_list|)
operator|==
literal|false
condition|)
block|{
name|fail
argument_list|(
literal|"Expected equality, testing property "
operator|+
name|property
operator|+
literal|"\nexpected: "
operator|+
name|toString
argument_list|(
name|ft1
argument_list|)
operator|+
literal|"; \nactual:   "
operator|+
name|toString
argument_list|(
name|ft2
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertFieldTypeNotEquals
specifier|protected
name|void
name|assertFieldTypeNotEquals
parameter_list|(
name|String
name|property
parameter_list|,
name|MappedFieldType
name|ft1
parameter_list|,
name|MappedFieldType
name|ft2
parameter_list|)
block|{
if|if
condition|(
name|ft1
operator|.
name|equals
argument_list|(
name|ft2
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Expected inequality, testing property "
operator|+
name|property
operator|+
literal|"\nfirst:  "
operator|+
name|toString
argument_list|(
name|ft1
argument_list|)
operator|+
literal|"; \nsecond: "
operator|+
name|toString
argument_list|(
name|ft2
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertCompatible
specifier|protected
name|void
name|assertCompatible
parameter_list|(
name|String
name|msg
parameter_list|,
name|MappedFieldType
name|ft1
parameter_list|,
name|MappedFieldType
name|ft2
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|conflicts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ft1
operator|.
name|checkCompatibility
argument_list|(
name|ft2
argument_list|,
name|conflicts
argument_list|,
name|strict
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Found conflicts for "
operator|+
name|msg
operator|+
literal|": "
operator|+
name|conflicts
argument_list|,
name|conflicts
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNotCompatible
specifier|protected
name|void
name|assertNotCompatible
parameter_list|(
name|String
name|msg
parameter_list|,
name|MappedFieldType
name|ft1
parameter_list|,
name|MappedFieldType
name|ft2
parameter_list|,
name|boolean
name|strict
parameter_list|,
name|String
modifier|...
name|messages
parameter_list|)
block|{
assert|assert
name|messages
operator|.
name|length
operator|!=
literal|0
assert|;
name|List
argument_list|<
name|String
argument_list|>
name|conflicts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ft1
operator|.
name|checkCompatibility
argument_list|(
name|ft2
argument_list|,
name|conflicts
argument_list|,
name|strict
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|message
range|:
name|messages
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|conflict
range|:
name|conflicts
control|)
block|{
if|if
condition|(
name|conflict
operator|.
name|contains
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Missing conflict for "
operator|+
name|msg
operator|+
literal|": ["
operator|+
name|message
operator|+
literal|"] in conflicts "
operator|+
name|conflicts
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|protected
name|String
name|toString
parameter_list|(
name|MappedFieldType
name|ft
parameter_list|)
block|{
return|return
literal|"MappedFieldType{"
operator|+
literal|"names="
operator|+
name|ft
operator|.
name|names
argument_list|()
operator|+
literal|", boost="
operator|+
name|ft
operator|.
name|boost
argument_list|()
operator|+
literal|", docValues="
operator|+
name|ft
operator|.
name|hasDocValues
argument_list|()
operator|+
literal|", indexAnalyzer="
operator|+
name|ft
operator|.
name|indexAnalyzer
argument_list|()
operator|+
literal|", searchAnalyzer="
operator|+
name|ft
operator|.
name|searchAnalyzer
argument_list|()
operator|+
literal|", searchQuoteAnalyzer="
operator|+
name|ft
operator|.
name|searchQuoteAnalyzer
argument_list|()
operator|+
literal|", similarity="
operator|+
name|ft
operator|.
name|similarity
argument_list|()
operator|+
literal|", normsLoading="
operator|+
name|ft
operator|.
name|normsLoading
argument_list|()
operator|+
literal|", fieldDataType="
operator|+
name|ft
operator|.
name|fieldDataType
argument_list|()
operator|+
literal|", nullValue="
operator|+
name|ft
operator|.
name|nullValue
argument_list|()
operator|+
literal|", nullValueAsString='"
operator|+
name|ft
operator|.
name|nullValueAsString
argument_list|()
operator|+
literal|"'"
operator|+
literal|"} "
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|testClone
specifier|public
name|void
name|testClone
parameter_list|()
block|{
name|MappedFieldType
name|fieldType
init|=
name|createNamedDefaultFieldType
argument_list|()
decl_stmt|;
name|MappedFieldType
name|clone
init|=
name|fieldType
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|clone
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clone
operator|.
name|getClass
argument_list|()
argument_list|,
name|fieldType
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clone
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clone
argument_list|,
name|clone
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
comment|// transitivity
for|for
control|(
name|Modifier
name|modifier
range|:
name|modifiers
control|)
block|{
name|fieldType
operator|=
name|createNamedDefaultFieldType
argument_list|()
expr_stmt|;
name|modifier
operator|.
name|modify
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
name|clone
operator|=
name|fieldType
operator|.
name|clone
argument_list|()
expr_stmt|;
name|assertNotSame
argument_list|(
name|clone
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
name|assertFieldTypeEquals
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|clone
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|MappedFieldType
name|ft1
init|=
name|createNamedDefaultFieldType
argument_list|()
decl_stmt|;
name|MappedFieldType
name|ft2
init|=
name|createNamedDefaultFieldType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ft1
argument_list|,
name|ft1
argument_list|)
expr_stmt|;
comment|// reflexive
name|assertEquals
argument_list|(
name|ft1
argument_list|,
name|ft2
argument_list|)
expr_stmt|;
comment|// symmetric
name|assertEquals
argument_list|(
name|ft2
argument_list|,
name|ft1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ft1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|ft2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Modifier
name|modifier
range|:
name|modifiers
control|)
block|{
name|ft1
operator|=
name|createNamedDefaultFieldType
argument_list|()
expr_stmt|;
name|ft2
operator|=
name|createNamedDefaultFieldType
argument_list|()
expr_stmt|;
name|modifier
operator|.
name|modify
argument_list|(
name|ft2
argument_list|)
expr_stmt|;
name|assertFieldTypeNotEquals
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft1
argument_list|,
name|ft2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|"hash code for modified property "
operator|+
name|modifier
operator|.
name|property
argument_list|,
name|ft1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|ft2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// modify the same property and they are equal again
name|modifier
operator|.
name|modify
argument_list|(
name|ft1
argument_list|)
expr_stmt|;
name|assertFieldTypeEquals
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft1
argument_list|,
name|ft2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hash code for modified property "
operator|+
name|modifier
operator|.
name|property
argument_list|,
name|ft1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|ft2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFreeze
specifier|public
name|void
name|testFreeze
parameter_list|()
block|{
for|for
control|(
name|Modifier
name|modifier
range|:
name|modifiers
control|)
block|{
name|MappedFieldType
name|fieldType
init|=
name|createNamedDefaultFieldType
argument_list|()
decl_stmt|;
name|fieldType
operator|.
name|freeze
argument_list|()
expr_stmt|;
try|try
block|{
name|modifier
operator|.
name|modify
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected already frozen exception for property "
operator|+
name|modifier
operator|.
name|property
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"already frozen"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testCheckTypeName
specifier|public
name|void
name|testCheckTypeName
parameter_list|()
block|{
specifier|final
name|MappedFieldType
name|fieldType
init|=
name|createNamedDefaultFieldType
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|conflicts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|fieldType
operator|.
name|checkTypeName
argument_list|(
name|fieldType
argument_list|,
name|conflicts
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|conflicts
operator|.
name|toString
argument_list|()
argument_list|,
name|conflicts
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|MappedFieldType
name|bogus
init|=
operator|new
name|MappedFieldType
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MappedFieldType
name|clone
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
name|fieldType
operator|.
name|typeName
argument_list|()
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|fieldType
operator|.
name|checkTypeName
argument_list|(
name|bogus
argument_list|,
name|conflicts
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected bad types exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Type names equal"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|conflicts
operator|.
name|toString
argument_list|()
argument_list|,
name|conflicts
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|MappedFieldType
name|other
init|=
operator|new
name|MappedFieldType
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MappedFieldType
name|clone
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
literal|"othertype"
return|;
block|}
block|}
decl_stmt|;
name|fieldType
operator|.
name|checkTypeName
argument_list|(
name|other
argument_list|,
name|conflicts
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|conflicts
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|conflicts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
literal|"cannot be changed from type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|conflicts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCheckCompatibility
specifier|public
name|void
name|testCheckCompatibility
parameter_list|()
block|{
name|MappedFieldType
name|ft1
init|=
name|createNamedDefaultFieldType
argument_list|()
decl_stmt|;
name|MappedFieldType
name|ft2
init|=
name|createNamedDefaultFieldType
argument_list|()
decl_stmt|;
name|assertCompatible
argument_list|(
literal|"default"
argument_list|,
name|ft1
argument_list|,
name|ft2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertCompatible
argument_list|(
literal|"default"
argument_list|,
name|ft1
argument_list|,
name|ft2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertCompatible
argument_list|(
literal|"default"
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertCompatible
argument_list|(
literal|"default"
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Modifier
name|modifier
range|:
name|modifiers
control|)
block|{
name|ft1
operator|=
name|createNamedDefaultFieldType
argument_list|()
expr_stmt|;
name|ft2
operator|=
name|createNamedDefaultFieldType
argument_list|()
expr_stmt|;
name|modifier
operator|.
name|normalizeOther
argument_list|(
name|ft1
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|modify
argument_list|(
name|ft2
argument_list|)
expr_stmt|;
if|if
condition|(
name|modifier
operator|.
name|strictOnly
condition|)
block|{
name|String
index|[]
name|conflicts
init|=
block|{
literal|"mapper [foo] is used by multiple types"
block|,
literal|"update ["
operator|+
name|modifier
operator|.
name|property
operator|+
literal|"]"
block|}
decl_stmt|;
name|assertCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft1
argument_list|,
name|ft2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft1
argument_list|,
name|ft2
argument_list|,
literal|true
argument_list|,
name|conflicts
argument_list|)
expr_stmt|;
name|assertCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// always symmetric when not strict
if|if
condition|(
name|modifier
operator|.
name|symmetric
condition|)
block|{
name|assertNotCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|true
argument_list|,
name|conflicts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// not compatible whether strict or not
name|String
name|conflict
init|=
literal|"different ["
operator|+
name|modifier
operator|.
name|property
operator|+
literal|"]"
decl_stmt|;
name|assertNotCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft1
argument_list|,
name|ft2
argument_list|,
literal|true
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
name|assertNotCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft1
argument_list|,
name|ft2
argument_list|,
literal|false
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
if|if
condition|(
name|modifier
operator|.
name|symmetric
condition|)
block|{
name|assertNotCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|true
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
name|assertNotCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|false
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertCompatible
argument_list|(
name|modifier
operator|.
name|property
argument_list|,
name|ft2
argument_list|,
name|ft1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

