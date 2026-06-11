import { Suspense } from "react";

import PaymentResultPageContent from "@/components/payment/PaymentResultPageContent";

export default function PaymentErrorPage() {
    return (
        <Suspense fallback={null}>
            <PaymentResultPageContent resultType="error" />
        </Suspense>
    );
}