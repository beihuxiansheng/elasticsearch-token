begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.docset
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|docset
package|;
end_package

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NotDocSet
specifier|public
class|class
name|NotDocSet
extends|extends
name|GetDocSet
block|{
DECL|field|set
specifier|private
specifier|final
name|DocSet
name|set
decl_stmt|;
DECL|method|NotDocSet
specifier|public
name|NotDocSet
parameter_list|(
name|DocSet
name|set
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|super
argument_list|(
name|max
argument_list|)
expr_stmt|;
name|this
operator|.
name|set
operator|=
name|set
expr_stmt|;
block|}
DECL|method|isCacheable
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
comment|// not cacheable, the reason is that by default, when constructing the filter, it is not cacheable,
comment|// so if someone wants it to be cacheable, we might as well construct a cached version of the result
return|return
literal|false
return|;
comment|//        return set.isCacheable();
block|}
DECL|method|get
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|!
name|set
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|sizeInBytes
annotation|@
name|Override
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|set
operator|.
name|sizeInBytes
argument_list|()
return|;
block|}
comment|// This seems like overhead compared to testing with get and iterating over docs
comment|//    @Override public DocIdSetIterator iterator() throws IOException {
comment|//        return new NotDocIdSetIterator();
comment|//    }
comment|//
comment|//    class NotDocIdSetIterator extends DocIdSetIterator {
comment|//        int lastReturn = -1;
comment|//        private DocIdSetIterator it1 = null;
comment|//        private int innerDocid = -1;
comment|//
comment|//        NotDocIdSetIterator() throws IOException {
comment|//            initialize();
comment|//        }
comment|//
comment|//        private void initialize() throws IOException {
comment|//            it1 = set.iterator();
comment|//
comment|//            if ((innerDocid = it1.nextDoc()) == DocIdSetIterator.NO_MORE_DOCS) it1 = null;
comment|//        }
comment|//
comment|//        @Override
comment|//        public int docID() {
comment|//            return lastReturn;
comment|//        }
comment|//
comment|//        @Override
comment|//        public int nextDoc() throws IOException {
comment|//            return advance(0);
comment|//        }
comment|//
comment|//        @Override
comment|//        public int advance(int target) throws IOException {
comment|//
comment|//            if (lastReturn == DocIdSetIterator.NO_MORE_DOCS) {
comment|//                return DocIdSetIterator.NO_MORE_DOCS;
comment|//            }
comment|//
comment|//            if (target<= lastReturn) target = lastReturn + 1;
comment|//
comment|//            if (it1 != null&& innerDocid< target) {
comment|//                if ((innerDocid = it1.advance(target)) == DocIdSetIterator.NO_MORE_DOCS) {
comment|//                    it1 = null;
comment|//                }
comment|//            }
comment|//
comment|//            while (it1 != null&& innerDocid == target) {
comment|//                target++;
comment|//                if (target>= max) {
comment|//                    return (lastReturn = DocIdSetIterator.NO_MORE_DOCS);
comment|//                }
comment|//                if ((innerDocid = it1.advance(target)) == DocIdSetIterator.NO_MORE_DOCS) {
comment|//                    it1 = null;
comment|//                }
comment|//            }
comment|//
comment|//            // ADDED THIS, bug in code
comment|//            if (target>= max) {
comment|//                return (lastReturn = DocIdSetIterator.NO_MORE_DOCS);
comment|//            }
comment|//
comment|//            return (lastReturn = target);
comment|//        }
comment|//    }
block|}
end_class

end_unit

