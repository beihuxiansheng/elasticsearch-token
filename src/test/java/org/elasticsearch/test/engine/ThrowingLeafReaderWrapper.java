begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|engine
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
name|index
operator|.
name|*
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
name|Bits
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
name|BytesRef
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
name|automaton
operator|.
name|CompiledAutomaton
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * An FilterLeafReader that allows to throw exceptions if certain methods  * are called on is. This allows to test parts of the system under certain  * error conditions that would otherwise not be possible.  */
end_comment

begin_class
DECL|class|ThrowingLeafReaderWrapper
specifier|public
class|class
name|ThrowingLeafReaderWrapper
extends|extends
name|FilterLeafReader
block|{
DECL|field|thrower
specifier|private
specifier|final
name|Thrower
name|thrower
decl_stmt|;
comment|/**      * Flags passed to {@link Thrower#maybeThrow(org.elasticsearch.test.engine.ThrowingLeafReaderWrapper.Flags)}      * when the corresponding method is called.      */
DECL|enum|Flags
specifier|public
enum|enum
name|Flags
block|{
DECL|enum constant|TermVectors
name|TermVectors
block|,
DECL|enum constant|Terms
name|Terms
block|,
DECL|enum constant|TermsEnum
name|TermsEnum
block|,
DECL|enum constant|Intersect
name|Intersect
block|,
DECL|enum constant|DocsEnum
name|DocsEnum
block|,
DECL|enum constant|DocsAndPositionsEnum
name|DocsAndPositionsEnum
block|,
DECL|enum constant|Fields
name|Fields
block|,
DECL|enum constant|Norms
DECL|enum constant|NumericDocValues
DECL|enum constant|BinaryDocValues
DECL|enum constant|SortedDocValues
DECL|enum constant|SortedSetDocValues
name|Norms
block|,
name|NumericDocValues
block|,
name|BinaryDocValues
block|,
name|SortedDocValues
block|,
name|SortedSetDocValues
block|;     }
comment|/**      * A callback interface that allows to throw certain exceptions for      * methods called on the IndexReader that is wrapped by {@link ThrowingLeafReaderWrapper}      */
DECL|interface|Thrower
specifier|public
specifier|static
interface|interface
name|Thrower
block|{
comment|/**          * Maybe throws an exception ;)          */
DECL|method|maybeThrow
specifier|public
name|void
name|maybeThrow
parameter_list|(
name|Flags
name|flag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**          * If this method returns true the {@link Terms} instance for the given field          * is wrapped with Thrower support otherwise no exception will be thrown for          * the current {@link Terms} instance or any other instance obtained from it.          */
DECL|method|wrapTerms
specifier|public
name|boolean
name|wrapTerms
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
block|}
DECL|method|ThrowingLeafReaderWrapper
specifier|public
name|ThrowingLeafReaderWrapper
parameter_list|(
name|LeafReader
name|in
parameter_list|,
name|Thrower
name|thrower
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|thrower
operator|=
name|thrower
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
name|Fields
name|fields
init|=
name|super
operator|.
name|fields
argument_list|()
decl_stmt|;
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|Fields
argument_list|)
expr_stmt|;
return|return
name|fields
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|ThrowingFields
argument_list|(
name|fields
argument_list|,
name|thrower
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|fields
init|=
name|super
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|TermVectors
argument_list|)
expr_stmt|;
return|return
name|fields
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|ThrowingFields
argument_list|(
name|fields
argument_list|,
name|thrower
argument_list|)
return|;
block|}
comment|/**      * Wraps a Fields but with additional asserts      */
DECL|class|ThrowingFields
specifier|public
specifier|static
class|class
name|ThrowingFields
extends|extends
name|FilterFields
block|{
DECL|field|thrower
specifier|private
specifier|final
name|Thrower
name|thrower
decl_stmt|;
DECL|method|ThrowingFields
specifier|public
name|ThrowingFields
parameter_list|(
name|Fields
name|in
parameter_list|,
name|Thrower
name|thrower
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|thrower
operator|=
name|thrower
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|super
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|thrower
operator|.
name|wrapTerms
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|Terms
argument_list|)
expr_stmt|;
return|return
name|terms
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|ThrowingTerms
argument_list|(
name|terms
argument_list|,
name|thrower
argument_list|)
return|;
block|}
return|return
name|terms
return|;
block|}
block|}
comment|/**      * Wraps a Terms but with additional asserts      */
DECL|class|ThrowingTerms
specifier|public
specifier|static
class|class
name|ThrowingTerms
extends|extends
name|FilterTerms
block|{
DECL|field|thrower
specifier|private
specifier|final
name|Thrower
name|thrower
decl_stmt|;
DECL|method|ThrowingTerms
specifier|public
name|ThrowingTerms
parameter_list|(
name|Terms
name|in
parameter_list|,
name|Thrower
name|thrower
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|thrower
operator|=
name|thrower
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|TermsEnum
name|intersect
parameter_list|(
name|CompiledAutomaton
name|automaton
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|termsEnum
init|=
name|in
operator|.
name|intersect
argument_list|(
name|automaton
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|Intersect
argument_list|)
expr_stmt|;
return|return
operator|new
name|ThrowingTermsEnum
argument_list|(
name|termsEnum
argument_list|,
name|thrower
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
name|TermsEnum
name|termsEnum
init|=
name|super
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|TermsEnum
argument_list|)
expr_stmt|;
return|return
operator|new
name|ThrowingTermsEnum
argument_list|(
name|termsEnum
argument_list|,
name|thrower
argument_list|)
return|;
block|}
block|}
DECL|class|ThrowingTermsEnum
specifier|static
class|class
name|ThrowingTermsEnum
extends|extends
name|FilterTermsEnum
block|{
DECL|field|thrower
specifier|private
specifier|final
name|Thrower
name|thrower
decl_stmt|;
DECL|method|ThrowingTermsEnum
specifier|public
name|ThrowingTermsEnum
parameter_list|(
name|TermsEnum
name|in
parameter_list|,
name|Thrower
name|thrower
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|thrower
operator|=
name|thrower
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postings
specifier|public
name|PostingsEnum
name|postings
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|PostingsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|flags
operator|&
name|PostingsEnum
operator|.
name|POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|DocsAndPositionsEnum
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|DocsEnum
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|postings
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|NumericDocValues
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|BinaryDocValues
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|SortedDocValues
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSetDocValues
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|SortedSetDocValues
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNormValues
specifier|public
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|thrower
operator|.
name|maybeThrow
argument_list|(
name|Flags
operator|.
name|Norms
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getNormValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
end_class

end_unit

