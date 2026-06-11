import { Suspense } from "react";

import PaymentResultPageContent from "@/components/payment/PaymentResultPageContent";

export default function PaymentFailedPage() {
    return (
        <Suspense fallback={null}>
            <PaymentResultPageContent resultType="failed" />
        </Suspense>
    );
}